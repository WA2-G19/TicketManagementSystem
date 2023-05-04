package it.polito.wa2.g19.server.ticketing.statuses

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.sql.Timestamp

@Repository
interface TicketStatusRepository: JpaRepository<TicketStatus, Int> {
    fun findAllByTicketId(ticketId: Int): Set<TicketStatus>


    @Query(value = "select ts from TicketStatus ts where ts.ticket.id = ?1 and ts.timestamp = (select max(ts2.timestamp) from TicketStatus ts2 where ts2.ticket.id = ?1)")
    fun findByTicketAndTimestampIsMaximum(ticketId: Int): TicketStatus

    @Query(value = "select count(*) as ticket_closed from ClosedTicketStatus ts where ts.by.id = ?1 and ts.ticket.status = ?2")
    fun getTicketsStatusByExpert(expertId: Int, ticketStatus: TicketStatusEnum): Int

    @Query(value = "select ts from TicketStatus ts where ts.ticket.expert.id = ?1 and ts.ticket.status=?2 or ts.ticket.status=?3 order by ts.ticket.id, ts.timestamp asc")
    fun getTicketStatusByExpert(expertId: Int, ticketStatusProgress: TicketStatusEnum, ticketStatusClosed: TicketStatusEnum): List<TicketStatus>

}