package it.polito.wa2.g19.server.ticketing.tickets

import it.polito.wa2.g19.server.ticketing.statuses.PriorityLevelEnum
import it.polito.wa2.g19.server.ticketing.statuses.TicketStatus
import it.polito.wa2.g19.server.ticketing.statuses.TicketStatusDTO
import it.polito.wa2.g19.server.ticketing.statuses.TicketStatusEnum

interface TicketService {
    fun getTicket(id: Int): TicketOutDTO

    fun getTickets(customerEmail: String?, expertEmail: String?, statusEnum: TicketStatusEnum?, priorityLevel: PriorityLevelEnum?): List<TicketOutDTO>

    fun createTicket(ticket: TicketDTO) : Int

    fun stopProgressTicket(ticketId: Int)

    fun reopenTicket(ticketId: Int)

    fun startProgressTicket(ticketId: Int, managerEmail: String, ticketStatus: TicketStatusDTO)

    fun resolveTicket(ticketId: Int, resolverEmail: String)

    fun closeTicket(ticketId: Int, closerEmail: String)

    fun getFinalStatus(ticketId: Int): TicketStatusDTO

}