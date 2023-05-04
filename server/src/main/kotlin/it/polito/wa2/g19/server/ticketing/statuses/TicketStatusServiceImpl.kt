package it.polito.wa2.g19.server.ticketing.statuses

import it.polito.wa2.g19.server.profiles.staff.Expert
import it.polito.wa2.g19.server.profiles.staff.Manager
import it.polito.wa2.g19.server.profiles.ProfileNotFoundException
import it.polito.wa2.g19.server.profiles.staff.StaffRepository
import it.polito.wa2.g19.server.ticketing.tickets.*
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDateTime

@Service
@Transactional

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
        var ticket: Ticket = ticketRepository.findByIdOrNull(ticketId) ?: throw TicketNotFoundException()

        val current = ticketStatusRepository.findByTicketAndTimestampIsMaximum(ticketId)
        if (current is InProgressTicketStatus) {
            ticketStatusRepository.save(OpenTicketStatus().apply {
                this.ticket = ticket
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
        var ticket: Ticket = ticketRepository.findByIdOrNull(ticketId) ?: throw TicketNotFoundException()


        if (ticket.status == TicketStatusEnum.Closed || ticket.status == TicketStatusEnum.Resolved) {
            ticket.status = TicketStatusEnum.Reopened
            ticket.statusHistory.add(ReopenedTicketStatus().apply {
                this.ticket = ticket
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
        var ticket: Ticket = ticketRepository.findByIdOrNull(ticketId) ?: throw TicketNotFoundException()

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
        var ticket: Ticket = ticketRepository.findByIdOrNull(ticketId) ?: throw TicketNotFoundException()

        val resolver = staffRepository.findByEmailIgnoreCase(resolverEmail) ?: throw ProfileNotFoundException()
        if (ticket.status == TicketStatusEnum.Open || ticket.status == TicketStatusEnum.Reopened || ticket.status == TicketStatusEnum.InProgress){
            ticket.status = TicketStatusEnum.Resolved
            ticket.statusHistory.add(ResolvedTicketStatus().apply {
                this.ticket = ticket
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
            ticket.statusHistory.add(ClosedTicketStatus().apply {
                this.ticket = ticket
                by = closer
            })
            ticketRepository.save(ticket)
        } else {
            throw InvalidTicketStatusTransitionException(ticket.status, TicketStatusEnum.Closed)
        }
    }

    override fun getTicketClosedByExpert(expertMail: String): Int {
        val expert = staffRepository.findByEmailIgnoreCase(expertMail) ?: throw ProfileNotFoundException()
        return ticketStatusRepository.getTicketsStatusByExpert(
            expert.getId()!!,
            TicketStatusEnum.Closed
        )
    }

    override fun getAverageTimedByExpert(expertMail: String): Float {
        val expert = staffRepository.findByEmailIgnoreCase(expertMail) ?: throw ProfileNotFoundException()
        val ticketStatusList = ticketStatusRepository.getTicketStatusByExpert(
            expert.getId()!!,
            TicketStatusEnum.InProgress,
            TicketStatusEnum.Closed)
        var diff = 0f
        var count = 0
        ticketStatusList.groupBy { it.ticket.getId()!! }.values.forEach {
            val normalizedSize = if(it.size%2 ==0) it.size else it.size - 1
            for(i in 0 until normalizedSize step 2) {
                diff += Duration.between(
                    it[i].timestamp,
                    it[i+1].timestamp
                ).seconds
                count ++
            }
        }
        return if(count == 0) 0f else diff/count
    }
}