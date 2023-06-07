package it.polito.wa2.g19.server.profiles.vendors

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface VendorRepository: JpaRepository<Vendor, UUID> {
    fun findByEmailIgnoreCase(email: String): Vendor?
}