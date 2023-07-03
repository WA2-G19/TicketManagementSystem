package it.polito.wa2.g19.server.products

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository: JpaRepository<Product, String> {

    override fun findAll(page: Pageable): Page<Product>
    fun findByEan(ean: String): Product?
}

