package it.polito.wa2.g19.server.ticketing.tickets

import it.polito.wa2.g19.server.common.EntityBase
import it.polito.wa2.g19.server.products.Product
import it.polito.wa2.g19.server.profiles.customers.Customer
import it.polito.wa2.g19.server.profiles.staff.Expert
import it.polito.wa2.g19.server.ticketing.statuses.PriorityLevel
import it.polito.wa2.g19.server.ticketing.statuses.TicketStatus
import it.polito.wa2.g19.server.ticketing.statuses.TicketStatusEnum
import jakarta.persistence.*

@Entity
@Table(name = "ticket")
class Ticket : EntityBase<Int>() {

    @ManyToOne
    @JoinColumn(nullable = false)
    lateinit var customer: Customer
    @ManyToOne
    @JoinColumn(nullable = false)
    lateinit var product: Product
    @OneToMany(mappedBy = "ticket", cascade = [CascadeType.ALL])
    lateinit var statusHistory: MutableSet<TicketStatus>
    @Column(nullable = false)
    var description: String = ""
    @Column(nullable = true)
    var status: TicketStatusEnum = TicketStatusEnum.Open
    @ManyToOne
    @JoinColumn(nullable = true)
    var priorityLevel: PriorityLevel? = null
    @ManyToOne
    @JoinColumn(nullable = true)
    var expert: Expert? = null


}