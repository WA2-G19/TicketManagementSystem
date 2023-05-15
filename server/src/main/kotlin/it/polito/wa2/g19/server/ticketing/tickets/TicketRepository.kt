package it.polito.wa2.g19.server.ticketing.tickets

import jakarta.validation.constraints.Email
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface TicketRepository: JpaRepository<Ticket, Int>, JpaSpecificationExecutor<Ticket> {

    fun findTicketByIdAndCustomerEmail(ticketId: Int, customerEmail: String): Ticket?
    fun findTicketByIdAndExpertEmail(ticketId: Int, expertEmail: String): Ticket?
}