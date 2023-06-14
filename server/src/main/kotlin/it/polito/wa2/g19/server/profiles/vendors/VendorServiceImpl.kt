package it.polito.wa2.g19.server.profiles.vendors

import it.polito.wa2.g19.server.profiles.DuplicateEmailException
import it.polito.wa2.g19.server.profiles.KeycloakException
import it.polito.wa2.g19.server.profiles.ProfileNotFoundException
import it.polito.wa2.g19.server.repositories.jpa.VendorRepository
import org.keycloak.admin.client.CreatedResponseUtil
import org.keycloak.admin.client.Keycloak
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional("transactionManager")
class VendorServiceImpl(
    private val vendorRepository: VendorRepository,
    private val keycloak: Keycloak,
    @Value("\${keycloak.admin.realm}")
    private val realmName: String
): VendorService {
    @PreAuthorize("hasRole('Manager')")
    override fun getAll(): List<VendorDTO> {
        return vendorRepository.findAll().map { it.toDTO() }
    }

    override fun getVendor(email: String): VendorDTO {
        return vendorRepository.findByEmailIgnoreCase(email)?.toDTO() ?: throw ProfileNotFoundException()
    }

    @PreAuthorize("hasRole('Manager')")
    override fun insertVendor(credentials: VendorCredentialsDTO) {
        val user = UserRepresentation().apply {
            username = credentials.vendor.email
            email = credentials.vendor.email
            isEnabled = true
            isEmailVerified = true
            realmRoles = listOf("Vendor")
            this.credentials = listOf(
                CredentialRepresentation().apply {
                    type = CredentialRepresentation.PASSWORD
                    value = credentials.password
                    isTemporary = false
                }
            )
        }

        val userResource = keycloak.realm(realmName).users()
        val creationResponse = userResource.create(user)
        if (creationResponse.status == HttpStatus.CONFLICT.value()) throw DuplicateEmailException()
        if (creationResponse.status != HttpStatus.CREATED.value()) throw KeycloakException()

        val userId = CreatedResponseUtil.getCreatedId(creationResponse)
        try{
            val profile = credentials.vendor
            val p = Vendor().apply {
                id = UUID.fromString(userId)
                email = profile.email.trim()
                businessName = profile.businessName
                phoneNumber = profile.phoneNumber
                address = profile.address
            }
            vendorRepository.save(p)
        } catch (e: Exception){
            userResource.get(userId).remove()
            throw KeycloakException()
        }
    }

    override fun updateVendor(email: String, profile: VendorDTO) {
        val p = vendorRepository.findByEmailIgnoreCase(email.trim().lowercase())

        if (p != null) {
            p.phoneNumber = profile.phoneNumber
            p.address = profile.address
            vendorRepository.save(p)
        } else {
            throw ProfileNotFoundException()
        }
    }
}