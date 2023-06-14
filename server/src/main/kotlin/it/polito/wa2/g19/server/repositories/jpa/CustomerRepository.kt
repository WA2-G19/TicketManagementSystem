package it.polito.wa2.g19.server.repositories.jpa

import it.polito.wa2.g19.server.profiles.customers.Customer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CustomerRepository: JpaRepository<Customer, UUID> {

    fun findByEmailIgnoreCase(email: String): Customer?

    fun existsByEmailIgnoreCase(email: String): Boolean
}
