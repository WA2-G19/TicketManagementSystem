package it.polito.wa2.g19.server.profiles.customers

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CustomerRepository: JpaRepository<Customer, Int> {

    fun findByEmailIgnoreCase(email: String): Customer?;

    fun existsByEmailIgnoreCase(email: String): Boolean;
}