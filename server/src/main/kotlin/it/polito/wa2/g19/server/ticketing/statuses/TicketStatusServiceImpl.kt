package it.polito.wa2.g19.server.ticketing.statuses

import it.polito.wa2.g19.server.profiles.Expert
import it.polito.wa2.g19.server.profiles.Manager
import it.polito.wa2.g19.server.profiles.ProfileNotFoundException
import it.polito.wa2.g19.server.profiles.StaffRepository
import it.polito.wa2.g19.server.ticketing.tickets.Ticket
import it.polito.wa2.g19.server.ticketing.tickets.TicketNotFoundException
import it.polito.wa2.g19.server.ticketing.tickets.TicketRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TicketStatusServiceImpl(
    private val ticketRepository: TicketRepository,
    private val ticketStatusRepository: TicketStatusRepository,
    private val staffRepository: StaffRepository
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
        val current = ticketStatusRepository.findByTicketAndTimestampIsMaximum(ticketId)
        if (current is ClosedTicketStatus || current is ResolvedTicketStatus) {
            ticketStatusRepository.save(ReopenedTicketStatus().apply {
                ticket = ticketRepository.findByIdOrNull(ticketId)!!
                timestamp = LocalDateTime.now()
            })
        } else {
            throw InvalidTicketStatusTransitionException(current.toDTO().status, TicketStatusEnum.Reopened)
        }
    }

    /*
    * grande!!
    * */
    override fun startProgressTicket(ticketId: Int, expertEmail: String, managerEmail: String) {
        if (!ticketRepository.existsById(ticketId)) {
            throw TicketNotFoundException()
        }
        val current = ticketStatusRepository.findByTicketAndTimestampIsMaximum(ticketId)
        val expert = staffRepository.findByEmailIgnoreCase(expertEmail) ?: throw ProfileNotFoundException()
        if (expert !is Expert) {
            throw ProfileNotFoundException()
        }
        val manager = staffRepository.findByEmailIgnoreCase(expertEmail) ?: throw ProfileNotFoundException()
        if (manager !is Manager) {
            throw ProfileNotFoundException()
        }
        if (current is OpenTicketStatus || current is ReopenedTicketStatus) {
            ticketStatusRepository.save(InProgressTicketStatus().apply {
                ticket = ticketRepository.findByIdOrNull(ticketId)!!
                this.expert = expert
                by = manager
                timestamp = LocalDateTime.now()
            })
        } else {
            throw InvalidTicketStatusTransitionException(current.toDTO().status, TicketStatusEnum.InProgress)
        }
    }

    /*
    * grande!!
    * */
    override fun resolveTicket(ticketId: Int, resolverEmail: String) {
        if (!ticketRepository.existsById(ticketId)) {
            throw TicketNotFoundException()
        }
        val current = ticketStatusRepository.findByTicketAndTimestampIsMaximum(ticketId)
        val resolver = staffRepository.findByEmailIgnoreCase(resolverEmail) ?: throw ProfileNotFoundException()
        if (current is OpenTicketStatus || current is ReopenedTicketStatus || current is InProgressTicketStatus) {
            ticketStatusRepository.save(ResolvedTicketStatus().apply {
                ticket = ticketRepository.findByIdOrNull(ticketId)!!
                by = resolver
                timestamp = LocalDateTime.now()
            })
        } else {
            throw InvalidTicketStatusTransitionException(current.toDTO().status, TicketStatusEnum.Resolved)
        }
    }

    /*
    * grande!!
    * */
    override fun closeTicket(ticketId: Int, closerEmail: String) {
        var ticket: Ticket = ticketRepository.findByIdOrNull(ticketId) ?: throw TicketNotFoundException()
        val closer = staffRepository.findByEmailIgnoreCase(closerEmail) ?: throw ProfileNotFoundException()
        if (ticket.status != TicketStatusEnum.Closed) {
            ticket.status = TicketStatusEnum.Closed
            ticket.expert = null
            ticket.priorityLevel = null
            ticket.statusHistory.add(ClosedTicketStatus().apply {
                by = closer
                timestamp = LocalDateTime.now()
            })
            ticketRepository.save(ticket)

        } else {
            throw InvalidTicketStatusTransitionException(ticket.status, TicketStatusEnum.Resolved)
        }
    }

}