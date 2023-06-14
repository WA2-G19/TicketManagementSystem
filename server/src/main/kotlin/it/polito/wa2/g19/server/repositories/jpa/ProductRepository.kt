package it.polito.wa2.g19.server.repositories.jpa

import it.polito.wa2.g19.server.products.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository: JpaRepository<Product, String> {

    fun findByEan(ean: String): Product?
}

