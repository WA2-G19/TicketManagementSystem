package it.polito.wa2.g19.server.repositories.jpa

import it.polito.wa2.g19.server.profiles.vendors.Vendor
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface VendorRepository: JpaRepository<Vendor, UUID> {
    fun findByEmailIgnoreCase(email: String): Vendor?
}