package it.polito.wa2.g19.server.ticketing.tickets

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface TicketRepository: JpaRepository<Ticket, Int>, JpaSpecificationExecutor<Ticket> {


}