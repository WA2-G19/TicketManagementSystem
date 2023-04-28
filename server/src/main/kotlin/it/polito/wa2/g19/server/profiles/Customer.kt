package it.polito.wa2.g19.server.profiles

import it.polito.wa2.g19.server.ticketing.tickets.Ticket
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "profile")
class Customer(): Profile() {

    @Column(nullable = false)
    var address: String = ""

    @OneToMany(mappedBy = "customer")

    lateinit var tickets: MutableSet<Ticket>

    constructor(email: String, name: String, surname: String, address: String) : this() {
        this.email = email
        this.name = name
        this.surname = surname
        this.address = address
    }
}
