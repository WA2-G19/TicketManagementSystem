package it.polito.wa2.g19.server.profiles.customers

import it.polito.wa2.g19.server.profiles.Profile
import it.polito.wa2.g19.server.ticketing.tickets.Ticket
import it.polito.wa2.g19.server.warranty.Warranty
import jakarta.persistence.*

@Entity
@Table(name = "profile")

class Customer(): Profile() {

    @Column(nullable = false)
    var address: String = ""

    @OneToMany(mappedBy = "customer")

    lateinit var warranties: MutableSet<Warranty>

    constructor(email: String, name: String, surname: String, address: String) : this() {
        this.email = email
        this.name = name
        this.surname = surname
        this.address = address
    }
}
