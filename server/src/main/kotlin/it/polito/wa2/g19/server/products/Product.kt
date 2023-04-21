package it.polito.wa2.g19.server.products

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.EAN

@Entity
@Table(name = "product")
class Product() {

    @Id
    @EAN
    @Size(min = 13, max = 13)
    var ean: String = ""
    @NotNull
    var name: String = ""
    @NotNull
    var brand: String = ""

    constructor(ean: String, name: String, brand: String): this() {
        this.ean = ean
        this.name = name
        this.brand = brand
    }
}