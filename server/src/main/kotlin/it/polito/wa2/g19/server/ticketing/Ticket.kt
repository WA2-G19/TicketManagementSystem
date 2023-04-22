package it.polito.wa2.g19.server.ticketing

import it.polito.wa2.g19.server.common.EntityBase
import it.polito.wa2.g19.server.products.Product
import it.polito.wa2.g19.server.profiles.Customer
import jakarta.persistence.*
import org.hibernate.annotations.JoinColumnOrFormula
import org.hibernate.annotations.JoinColumnsOrFormulas
import org.hibernate.annotations.JoinFormula

@Entity
@Table(name = "ticket")
class Ticket(): EntityBase<Int>() {

    @ManyToOne
    @JoinColumn(nullable = false)
    lateinit var customer: Customer
    @ManyToOne
    @JoinColumn(nullable = false)
    lateinit var product: Product

    @OneToMany(mappedBy = "ticket")
    lateinit var statusHistory: MutableSet<TicketStatus>

    @ManyToOne
    @JoinColumnsOrFormulas(
        JoinColumnOrFormula(formula = JoinFormula(value = "(SELECT ts.id FROM TicketStatus ts WHERE ts.timestamp = (SELECT MAX(ts2.timestamp) FROM TicketStatus ts2 WHERE ts2.ticket_id = id) AND ts.ticket_id = id", referencedColumnName = "id"))
    )
    lateinit var status: TicketStatus

    @ManyToOne
    @JoinColumnsOrFormulas(
        JoinColumnOrFormula(formula = JoinFormula(value = "(SELECT ts.priority FROM TicketStatus ts WHERE ts.timestamp = (SELECT MAX(ts2.timestamp) FROM TicketStatus ts2 WHERE ts2.ticket_id = id AND ts2.priority IS NOT NULL) AND ts.ticket_id = id", referencedColumnName = "id"))
    )
    var priorityLevel: PriorityLevel? = null

    @Column(nullable = false)
    var description: String = ""
}