package it.polito.wa2.g19.server.ticketing

import it.polito.wa2.g19.server.profiles.Customer
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

@Entity
@Table(name = "ticket")
class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Int? = null

    @ManyToOne
    lateinit var customer: Customer

    @NotNull(message = "description cannot be null")
    var description: String = ""
}