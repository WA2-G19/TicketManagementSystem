package it.polito.wa2.g19.server.ticketing.statuses

import it.polito.wa2.g19.server.profiles.Expert
import it.polito.wa2.g19.server.profiles.Manager
import it.polito.wa2.g19.server.profiles.ProfileNotFoundException
import it.polito.wa2.g19.server.profiles.StaffRepository
import it.polito.wa2.g19.server.ticketing.tickets.*
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TicketStatusServiceImpl(
    private val ticketRepository: TicketRepository,
    private val ticketStatusRepository: TicketStatusRepository,
    private val staffRepository: StaffRepository,
    private val priorityLevelRepository: PriorityLevelRepository
): TicketStatusService {


    /*
    chiedere a Malnati se sia possibile farlo, forse non può essere chiamata da nessuno,
    sicuramente non dai customer.
     */
    override fun getStatusHistory(ticketId: Int): Set<TicketStatusDTO> {
        if (!ticketRepository.existsById(ticketId)) {
            throw TicketNotFoundException()
        }
        return ticketStatusRepository.findAllByTicketId(ticketId).map {
            it.toDTO()
        }.toSet()
    }

    /*
    * Probabilmente ridondante, meglio mettere lo stato nel ticket e quindi ritornare con la getTicket.
    * Magari gestire la validazione con un TicketInDTO e TicketOutDTO che estendono TicketDTO?
    * */
    override fun getCurrentStatus(ticketId: Int): TicketStatusDTO {
        if (!ticketRepository.existsById(ticketId)) {
            throw TicketNotFoundException()
        }
        return ticketStatusRepository.findByTicketAndTimestampIsMaximum(ticketId).toDTO()
    }

    override fun stopProgressTicket(ticketId: Int) {
        if (!ticketRepository.existsById(ticketId)) {
            throw TicketNotFoundException()
        }
        val current = ticketStatusRepository.findByTicketAndTimestampIsMaximum(ticketId)
        if (current is InProgressTicketStatus) {
            ticketStatusRepository.save(OpenTicketStatus().apply {
                ticket = ticketRepository.findByIdOrNull(ticketId)!!
                timestamp = LocalDateTime.now()
            })
        } else {
            throw InvalidTicketStatusTransitionException(current.toDTO().status, TicketStatusEnum.Open)
        }
    }

    /*
        grande!!
     */
    override fun reopenTicket(ticketId: Int) {
        if (!ticketRepository.existsById(ticketId)) {
            throw TicketNotFoundException()
        }
        var ticket = ticketRepository.findByIdOrNull(ticketId)!!

        if (ticket.status == TicketStatusEnum.Closed || ticket.status == TicketStatusEnum.Resolved) {
            ticket.status = TicketStatusEnum.Reopened
            ticket.statusHistory.add(ReopenedTicketStatus().apply {
                timestamp = LocalDateTime.now()
            })
            ticketRepository.save(ticket)
        } else {
            throw InvalidTicketStatusTransitionException(ticket.toOutDTO().status, TicketStatusEnum.Reopened)
        }
    }



    /*
    * grande!!
    * */
    override fun startProgressTicket(ticketId: Int, managerEmail: String, ticketStatus: TicketStatusDTO) {
        if (!ticketRepository.existsById(ticketId)) {
            throw TicketNotFoundException()
        }
        val ticket = ticketRepository.findByIdOrNull(ticketId)!!
        val expert = staffRepository.findByEmailIgnoreCase(ticketStatus.expert!!) ?: throw ProfileNotFoundException()
        if (expert !is Expert) {
            throw ProfileNotFoundException()
        }
        val manager = staffRepository.findByEmailIgnoreCase(managerEmail) ?: throw ProfileNotFoundException()
        if (manager !is Manager) {
            throw ProfileNotFoundException()
        }
        if (ticket.status == TicketStatusEnum.Open || ticket.status == TicketStatusEnum.Reopened) {
            ticket.expert = expert
            ticket.status = TicketStatusEnum.InProgress
            ticket.priorityLevel = priorityLevelRepository.findByName(ticketStatus.priorityLevel!!.name)
            ticket.statusHistory.add(InProgressTicketStatus().apply {
                this.ticket = ticket
                this.expert = expert
                by = manager
                timestamp = LocalDateTime.now()
                priority = ticket.priorityLevel!!
            })
            ticketRepository.save(ticket)

        } else {
            throw InvalidTicketStatusTransitionException(ticket.status, TicketStatusEnum.InProgress)
        }
    }

    /*
    * grande!!
    * */
    override fun resolveTicket(ticketId: Int, resolverEmail: String) {
        if (!ticketRepository.existsById(ticketId)) {
            throw TicketNotFoundException()
        }
        val ticket = ticketRepository.findByIdOrNull(ticketId)!!
        val resolver = staffRepository.findByEmailIgnoreCase(resolverEmail) ?: throw ProfileNotFoundException()
        if (ticket.status == TicketStatusEnum.Open || ticket.status == TicketStatusEnum.Reopened || ticket.status == TicketStatusEnum.InProgress){
            ticket.status = TicketStatusEnum.Resolved
            ticket.statusHistory.add(ResolvedTicketStatus().apply {
                by = resolver
                timestamp = LocalDateTime.now()
            })
            ticketRepository.save(ticket)
        } else {
            throw InvalidTicketStatusTransitionException(ticket.status, TicketStatusEnum.Resolved)
        }
    }

    /*
    * grande!!
    * */
    @Transactional
    override fun closeTicket(ticketId: Int, closerEmail: String) {
        var ticket: Ticket = ticketRepository.findByIdOrNull(ticketId) ?: throw TicketNotFoundException()
        val closer = staffRepository.findByEmailIgnoreCase(closerEmail) ?: throw ProfileNotFoundException()
        if (ticket.status != TicketStatusEnum.Closed) {
            ticket.status = TicketStatusEnum.Closed
            ticket.expert = null
            ticket.priorityLevel = null
            ticketStatusRepository.save(ClosedTicketStatus().apply {
                ticket = ticket
                by = closer
            })
            ticketRepository.save(ticket)
        } else {
            throw InvalidTicketStatusTransitionException(ticket.status, TicketStatusEnum.Closed)
        }

    }

}