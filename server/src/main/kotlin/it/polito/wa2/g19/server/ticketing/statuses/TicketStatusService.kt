package it.polito.wa2.g19.server.ticketing.statuses

interface TicketStatusService {
    fun getStatusHistory(ticketId: Int): Set<TicketStatusDTO>

    fun getCurrentStatus(ticketId: Int): TicketStatusDTO

    fun stopProgressTicket(ticketId: Int)

    fun reopenTicket(ticketId: Int)

    fun startProgressTicket(ticketId: Int, managerEmail: String, ticketStatus: TicketStatusDTO)

    fun resolveTicket(ticketId: Int, resolverEmail: String)

    fun closeTicket(ticketId: Int, closerEmail: String)

    fun getTicketClosedByExpert(expertMail: String): Int

    fun getAverageTimedByExpert(expertMail: String): Float

}