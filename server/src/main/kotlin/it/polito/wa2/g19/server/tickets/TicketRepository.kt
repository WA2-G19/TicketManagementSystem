package it.polito.wa2.g19.server.tickets

import it.polito.wa2.g19.server.tickets.Ticket
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TicketRepository: JpaRepository<Ticket, Int> {
}