package it.polito.wa2.g19.server.ticketing.statuses

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface TicketStatusRepository: JpaRepository<TicketStatus, Int> {
    fun findAllByTicketId(ticketId: Int): Set<TicketStatus>

    @Query(value = "select ts from TicketStatus ts where ts.ticket.id = ?1 and ts.timestamp = (select max(ts2.timestamp) from TicketStatus ts2 where ts2.ticket.id = ?1)")
    fun findByTicketAndTimestampIsMaximum(ticketId: Int): TicketStatus

    @Query(value = "select count(*) as ticket_closed from ClosedTicketStatus ts where ts.by.email = ?1")
    fun getTicketsClosedByExpert(expertEmail: String): Int

    @Query(value = "select ts from TicketStatus ts join fetch ts.ticket where (ts.id IN (SELECT ots.id from InProgressTicketStatus ots where ots.expert.email = ?1) or (ts.id IN (SELECT cts.id from ClosedTicketStatus cts where cts.by.email = ?1))) order by ts.ticket.id, ts.timestamp asc")
    fun getTicketStatusByExpert(expertEmail: String): List<TicketStatus>

}


