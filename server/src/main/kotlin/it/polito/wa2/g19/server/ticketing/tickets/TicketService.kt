package it.polito.wa2.g19.server.ticketing.tickets

import it.polito.wa2.g19.server.ticketing.statuses.PriorityLevel
import it.polito.wa2.g19.server.ticketing.statuses.PriorityLevelEnum
import it.polito.wa2.g19.server.ticketing.statuses.TicketStatusEnum

interface TicketService {
    fun getTicket(id: Int): TicketOutDTO

    fun getTickets(customerEmail: String?, expertEmail: String?, statusEnum: TicketStatusEnum?, priorityLevel: PriorityLevelEnum?): List<TicketOutDTO>

    fun createTicket(ticket: TicketDTO) : Int
}