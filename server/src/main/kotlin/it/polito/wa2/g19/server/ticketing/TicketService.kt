package it.polito.wa2.g19.server.ticketing

interface TicketService {
    fun getTicket(id: Int): TicketDTO

    fun getTickets(): Set<TicketDTO>

    fun openTicket(ticket: TicketDTO)

    fun closeTicket(ticket: TicketDTO)

    fun resolveTicket(ticket: TicketDTO)

    fun inProgressTicket(ticket: TicketDTO)

    fun reopenTicket(ticket: TicketDTO)
}