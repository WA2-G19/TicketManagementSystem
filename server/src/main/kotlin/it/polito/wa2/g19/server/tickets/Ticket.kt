package it.polito.wa2.g19.server.tickets

import it.polito.wa2.g19.server.common.EntityBase
import it.polito.wa2.g19.server.products.Product
import it.polito.wa2.g19.server.profiles.Customer
import it.polito.wa2.g19.server.tickets.statuses.TicketStatus
import jakarta.persistence.*

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
    @Column(nullable = false)
    var description: String = ""
}