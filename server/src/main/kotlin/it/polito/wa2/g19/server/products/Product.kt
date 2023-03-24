package it.polito.wa2.g19.server.products

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "product")
class Product {

    @Id
    var ean: String = ""
    var name: String = ""
    var brand: String = ""

}