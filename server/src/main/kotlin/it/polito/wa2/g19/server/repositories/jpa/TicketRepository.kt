package it.polito.wa2.g19.server.repositories.jpa

import it.polito.wa2.g19.server.ticketing.tickets.Ticket
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface TicketRepository: JpaRepository<Ticket, Int>, JpaSpecificationExecutor<Ticket> {

    @Query("select t from Ticket t where t.id = ?1 and t.warranty.customer.email = ?2")
    fun findTicketByIdAndCustomerEmail(ticketId: Int, customerEmail: String): Ticket?
    @Query("select t from Ticket t where t.id = ?1 and t.expert.email = ?2")
    fun findTicketByIdAndExpertEmail(ticketId: Int, expertEmail: String): Ticket?
}