package it.polito.wa2.g19.server.ticketing.statuses

import it.polito.wa2.g19.server.common.EntityBase
import it.polito.wa2.g19.server.profiles.staff.Expert
import it.polito.wa2.g19.server.profiles.staff.Staff
import it.polito.wa2.g19.server.profiles.staff.Manager
import it.polito.wa2.g19.server.ticketing.tickets.Ticket
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity
@Table(
    name = "ticket_status",
    indexes = [
        Index(name = "IX_ticket_status_timestamp", columnList = "timestamp DESC"),
        Index(name = "IX_ticket_status_ticket_id", columnList = "ticket_id DESC")
    ]
)
abstract class TicketStatus : EntityBase<Int>(), Comparable<TicketStatus> {

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    open lateinit var ticket: Ticket

    @CreationTimestamp
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    open var timestamp: LocalDateTime = LocalDateTime.now()

    override fun compareTo(other: TicketStatus): Int {
        return this.timestamp.nano - other.timestamp.nano
    }

    abstract fun toDTO(): TicketStatusDTO
}

@Entity
@DiscriminatorValue("Open")
class OpenTicketStatus : TicketStatus() {
    override fun toDTO() = TicketStatusDTO(ticket.getId()!!, TicketStatusEnum.Open, null, null, null, timestamp)
}

@Entity
@DiscriminatorValue("Resolved")
class ResolvedTicketStatus : TicketStatus() {
    @ManyToOne
    lateinit var by: Staff

    override fun toDTO() = TicketStatusDTO(ticket.getId()!!, TicketStatusEnum.Resolved, null, by.email, null, timestamp)
}

@Entity
@DiscriminatorValue("Closed")
class ClosedTicketStatus : TicketStatus() {
    @ManyToOne
    lateinit var by: Staff

    override fun toDTO() = TicketStatusDTO(ticket.getId()!!, TicketStatusEnum.Closed, null, by.email, null, timestamp)
}

@Entity
@DiscriminatorValue("InProgress")
class InProgressTicketStatus : TicketStatus() {

    @ManyToOne
    lateinit var expert: Expert
    @ManyToOne
    lateinit var by: Manager
    @ManyToOne
    var priority: PriorityLevel = PriorityLevel()

    override fun toDTO() = TicketStatusDTO(ticket.getId()!!, TicketStatusEnum.InProgress, expert.email, by.email, PriorityLevelEnum.valueOf(priority.name), timestamp)
}

@Entity
@DiscriminatorValue("Reopened")
class ReopenedTicketStatus : TicketStatus() {

    override fun toDTO() = TicketStatusDTO(ticket.getId()!!, TicketStatusEnum.Reopened, null, null, null, timestamp)
}

@Entity
@Table(name = "priority_level")
class PriorityLevel() {

    @Id
    var name: String = ""

}



enum class PriorityLevelEnum{
    LOW, MEDIUM, HIGH, CRITICAL;
}