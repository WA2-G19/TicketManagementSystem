package it.polito.wa2.g19.server.ticketing

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TicketStatusRepository: JpaRepository<TicketStatus, Int> {
}