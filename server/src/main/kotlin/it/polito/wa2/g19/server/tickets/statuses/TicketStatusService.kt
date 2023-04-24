package it.polito.wa2.g19.server.tickets.statuses

interface TicketStatusService {
    fun getStatusHistory(ticketId: Int): Set<TicketStatusDTO>

    fun getCurrentStatus(ticketId: Int): TicketStatusDTO

    fun reopenTicket(ticketId: Int)

    fun inProgressTicket(ticketId: Int, expertEmail: String, managerEmail: String)

    fun resolveTicket(ticketId: Int, resolverEmail: String)

    fun closeTicket(ticketId: Int, closerEmail: String)
}