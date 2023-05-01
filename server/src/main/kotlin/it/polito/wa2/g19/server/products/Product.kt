package it.polito.wa2.g19.server.products

import it.polito.wa2.g19.server.common.EntityBase
import it.polito.wa2.g19.server.ticketing.tickets.Ticket
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.OneToMany
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.EAN

@Entity
@Table(name = "product")
class Product(): EntityBase<Int>() {

    @OneToMany(mappedBy = "product")
    lateinit var tickets: Set<Ticket>

    @EAN
    @Size(min = 13, max = 13)
    @Column(unique = true, nullable = false)
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