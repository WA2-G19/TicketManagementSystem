package it.polito.wa2.g19.server.products

import it.polito.wa2.g19.server.ticketing.tickets.Ticket
import it.polito.wa2.g19.server.warranty.Warranty
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.EAN

@Entity
@Table(name = "product")
class Product() {

    @OneToMany(mappedBy = "product")
    lateinit var warranties: Set<Warranty>

    @Id
    @EAN
    @Size(min = 13, max = 13)
    var ean: String = ""
    @Column(nullable = false)
    var name: String = ""
    @Column(nullable = false)
    var brand: String = ""

    constructor(ean: String, name: String, brand: String): this() {
        this.ean = ean
        this.name = name
        this.brand = brand
    }
}