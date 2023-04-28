package it.polito.wa2.g19.server.ticketing.statuses

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface TicketStatusRepository: JpaRepository<TicketStatus, Int> {
    fun findAllByTicketId(ticketId: Int): Set<TicketStatus>

    @Query(value = "select ts from TicketStatus ts where ts.ticket.id = ?1 and ts.timestamp = (select max(ts2.timestamp) from TicketStatus ts2 where ts2.ticket.id = ?1)")
    fun findByTicketAndTimestampIsMaximum(ticketId: Int): TicketStatus
}