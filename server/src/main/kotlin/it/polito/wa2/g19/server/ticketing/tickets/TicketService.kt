package it.polito.wa2.g19.server.ticketing.tickets

interface TicketService {
    fun getTicket(id: Int): TicketDTO

    fun getTickets(): Set<TicketDTO>

    fun createTicket(ticket: TicketDTO) : Int
}