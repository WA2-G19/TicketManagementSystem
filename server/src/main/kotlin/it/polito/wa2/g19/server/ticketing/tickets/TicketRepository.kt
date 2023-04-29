package it.polito.wa2.g19.server.ticketing.tickets

import it.polito.wa2.g19.server.profiles.Customer
import it.polito.wa2.g19.server.profiles.Expert
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface TicketRepository: JpaRepository<Ticket, Int>, JpaSpecificationExecutor<Ticket> {


}