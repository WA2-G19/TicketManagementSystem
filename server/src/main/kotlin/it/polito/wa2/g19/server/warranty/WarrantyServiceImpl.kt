package it.polito.wa2.g19.server.warranty

import it.polito.wa2.g19.server.common.Role
import it.polito.wa2.g19.server.products.ProductNotFoundException
import it.polito.wa2.g19.server.products.ProductRepository
import it.polito.wa2.g19.server.profiles.ProfileNotFoundException
import it.polito.wa2.g19.server.profiles.customers.CustomerRepository
import it.polito.wa2.g19.server.profiles.vendors.VendorRepository
import it.polito.wa2.g19.server.ticketing.tickets.ForbiddenException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional("transactionManager")
class WarrantyServiceImpl(
    private val warrantyRepository: WarrantyRepository,
    private val vendorRepository: VendorRepository,
    private val customerRepository: CustomerRepository,
    private val productRepository: ProductRepository
): WarrantyService {



    @PreAuthorize("hasAnyRole('Client', 'Manager', 'Vendor')")
    override fun getAll(): List<WarrantyOutDTO> {
        val principal = SecurityContextHolder.getContext().authentication
        return if (principal.authorities.any { it.authority == Role.ROLE_Client.name }) {
            warrantyRepository.findByCustomerEmail(principal.name).map { it.toDTO() }
        } else if (principal.authorities.any { it.authority == Role.ROLE_Manager.name }) {
            warrantyRepository.findAll().map { it.toDTO() }
        } else if (principal.authorities.any { it.authority == Role.ROLE_Vendor.name }) {
            warrantyRepository.findByVendorEmail(principal.name).map { it.toDTO() }
        } else {
            throw ForbiddenException()
        }
    }


    @PreAuthorize("hasAnyRole('Client', 'Manager', 'Vendor')")
    override fun getById(id: UUID): WarrantyOutDTO {
        val principal = SecurityContextHolder.getContext().authentication
        return if (principal.authorities.any { it.authority == Role.ROLE_Client.name }) {
            warrantyRepository.findByIdAndCustomerEmail(id, principal.name)?.toDTO() ?: throw WarrantyNotFoundException()
        } else if (principal.authorities.any { it.authority == Role.ROLE_Manager.name }) {
            warrantyRepository.findByIdOrNull(id)?.toDTO() ?: throw WarrantyNotFoundException()
        } else if (principal.authorities.any { it.authority == Role.ROLE_Vendor.name }) {
            warrantyRepository.findByIdAndVendorEmail(id, principal.name)?.toDTO() ?: throw WarrantyNotFoundException()
        } else {
            throw ForbiddenException()
        }
    }



    @PreAuthorize("hasRole('Vendor')")
    override fun insertWarranty(warranty: WarrantyInDTO): WarrantyOutDTO {
        val principal = SecurityContextHolder.getContext().authentication

        val v = vendorRepository.findByEmailIgnoreCase(principal.name) ?: throw ProfileNotFoundException()
        val p = productRepository.findByEan(warranty.productEan) ?: throw ProductNotFoundException()
        val w = Warranty().apply {
            vendor = v
            product = p
            duration = warranty.duration
        }
        return warrantyRepository.save(w).toDTO()
    }

    @PreAuthorize("hasRole('Client')")
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

    @PreAuthorize("hasRole('Vendor')")
    override fun deleteWarranty(warrantyId: UUID){
        val w = warrantyRepository.findByIdOrNull(warrantyId) ?: throw WarrantyNotFoundException()
        if (w.customer != null) throw WarrantyAlreadyActivated()
        warrantyRepository.delete(w)
    }
}