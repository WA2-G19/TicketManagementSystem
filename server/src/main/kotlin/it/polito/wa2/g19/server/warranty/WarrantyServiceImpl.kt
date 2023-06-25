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
        return when(Role.valueOf(principal.authorities.iterator().next().authority)){
            Role.ROLE_Client-> warrantyRepository.findByCustomerEmail(principal.name)
            Role.ROLE_Manager -> warrantyRepository.findAll()
            Role.ROLE_Vendor -> warrantyRepository.findByVendorEmail(principal.name)
            else -> throw ForbiddenException()
        }.map { it.toDTO() }
    }


    @PreAuthorize("hasAnyRole('Client', 'Manager', 'Vendor')")
    override fun getById(id: UUID): WarrantyOutDTO {
        val principal = SecurityContextHolder.getContext().authentication
        return when(Role.valueOf(principal.authorities.iterator().next().authority)){
            Role.ROLE_Client -> warrantyRepository.findByIdAndCustomerEmail(id, principal.name)
            Role.ROLE_Manager -> warrantyRepository.findByIdOrNull(id)
            Role.ROLE_Vendor -> warrantyRepository.findByIdAndVendorEmail(id, principal.name)
            else -> throw ForbiddenException()
        }?.toDTO() ?: throw WarrantyNotFoundException()

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