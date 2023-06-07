package it.polito.wa2.g19.server.warranty

import it.polito.wa2.g19.server.products.ProductNotFoundException
import it.polito.wa2.g19.server.products.ProductRepository
import it.polito.wa2.g19.server.profiles.ProfileNotFoundException
import it.polito.wa2.g19.server.profiles.customers.CustomerRepository
import it.polito.wa2.g19.server.profiles.vendors.VendorRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class WarrantyServiceImpl(
    private val warrantyRepository: WarrantyRepository,
    private val vendorRepository: VendorRepository,
    private val customerRepository: CustomerRepository,
    private val productRepository: ProductRepository
): WarrantyService {
    override fun getAll(): List<WarrantyOutDTO> {
        return warrantyRepository.findAll().map { it.toDTO() }
    }

    override fun getById(id: UUID): WarrantyOutDTO {
        return warrantyRepository.findByIdOrNull(id)?.toDTO() ?: throw WarrantyNotFoundException()
    }

    override fun getByCustomerEmail(email: String): List<WarrantyOutDTO> {
        return warrantyRepository.findByCustomerEmail(email).map { it.toDTO() }
    }

    override fun getByCustomerId(id: UUID): List<WarrantyOutDTO> {
        return warrantyRepository.findByCustomerId(id).map { it.toDTO() }
    }

    override fun getByVendorEmail(email: String): List<WarrantyOutDTO> {
        return warrantyRepository.findByVendorEmail(email).map { it.toDTO() }
    }

    override fun getByVendorId(id: UUID): List<WarrantyOutDTO> {
        return warrantyRepository.findByVendorId(id).map { it.toDTO() }
    }

    @PreAuthorize("hasRole('Vendor')")
    override fun insertWarranty(warranty: WarrantyInDTO): WarrantyOutDTO {
        val v = vendorRepository.findByEmailIgnoreCase(warranty.vendorEmail) ?: throw ProfileNotFoundException()
        val p = productRepository.findByEan(warranty.productEan) ?: throw ProductNotFoundException()
        val w = Warranty().apply {
            vendor = v
            product = p
            duration = warranty.duration
        }
        return warrantyRepository.save(w).toDTO()
    }

    @PreAuthorize("hasRole('Customer')")
    override fun activateWarranty(warrantyId: UUID, customerEmail: String): WarrantyOutDTO {
        val w = warrantyRepository.findByIdOrNull(warrantyId) ?: throw WarrantyNotFoundException()
        if (w.customer != null) throw WarrantyAlreadyActivated()
        if (w.creationTimestamp + w.duration < LocalDateTime.now()) throw WarrantyExpiredException()
        val c = customerRepository.findByEmailIgnoreCase(customerEmail) ?: throw ProfileNotFoundException()
        w.apply {
            customer = c
            activationTimestamp = LocalDateTime.now()
        }
        return warrantyRepository.save(w).toDTO()
    }
}