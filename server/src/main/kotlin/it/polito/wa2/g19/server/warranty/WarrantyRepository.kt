package it.polito.wa2.g19.server.warranty

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface WarrantyRepository: JpaRepository<Warranty, UUID> {

    @Query("select w from Warranty w where w.customer.email = ?1")
    fun findByCustomerEmail(email: String): List<Warranty>

    @Query("select w from Warranty w where w.customer.id = ?1")
    fun findByCustomerId(id: UUID): List<Warranty>

    @Query("select w from Warranty w where w.vendor.email = ?1")
    fun findByVendorEmail(email: String): List<Warranty>

    @Query("select w from Warranty w where w.vendor.id = ?1")
    fun findByVendorId(id: UUID): List<Warranty>

    @Query("select w from Warranty w where w.customer.id = ?1 and w.product.ean = ?2")
    fun findWarrantyByCustomerIdAndProductEan(customerId: UUID, productEan: String): Warranty?
}