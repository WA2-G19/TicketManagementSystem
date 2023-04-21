package it.polito.wa2.g19.server.ticketing

import it.polito.wa2.g19.server.profiles.Expert
import it.polito.wa2.g19.server.profiles.Staff
import it.polito.wa2.g19.server.profiles.Manager
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
abstract class TicketStatus() {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    open var id: Int? = null

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    open lateinit var ticket: Ticket

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    open var timestamp: LocalDateTime = LocalDateTime.now()
}

@Entity
class OpenTicketStatus(): TicketStatus() {

}

@Entity
class ResolvedTicketStatus(): TicketStatus() {
    @ManyToOne
    lateinit var by: Staff
}

@Entity
class ClosedTicketStatus(): TicketStatus() {
    @ManyToOne
    lateinit var by: Staff
}

@Entity
class InProgressTicketStatus(): TicketStatus() {
    @ManyToOne
    lateinit var expert: Expert
    @ManyToOne
    lateinit var by: Manager
    @ManyToOne
    var priority: PriorityLevel = PriorityLevel()
}

@Entity
class ReopenedTicketStatus(): TicketStatus() {

}

@Entity
@Table(name = "priority_level")
class PriorityLevel {
    @Id
    var name: String = ""
}