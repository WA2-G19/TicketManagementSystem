package it.polito.wa2.g19.server.profiles.customers

import it.polito.wa2.g19.server.profiles.DuplicateEmailException
import it.polito.wa2.g19.server.profiles.ProfileNotFoundException
import org.keycloak.admin.client.Keycloak
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CustomerServiceImpl(
    private val customerRepository: CustomerRepository
): CustomerService {

    @Autowired
    private lateinit var keycloak: Keycloak

    @Value("\${keycloak.admin.realm}")
    private lateinit var realmName: String

    @PreAuthorize("hasRole('Manager')")
    override fun getAll(): List<CustomerDTO> {
        return customerRepository.findAll().map { it.toDTO() }
    }

    @PreAuthorize("isAuthenticated() and (hasRole('Manager') or #email == principal.username)")
    override fun getProfile(email: String): CustomerDTO {

        val profile = customerRepository.findByEmailIgnoreCase(email.trim().lowercase())
        if (profile == null) {
            throw ProfileNotFoundException()
        } else {
            return profile.toDTO()
        }
    }

    @PreAuthorize("hasRole('Manager')")
    override fun insertProfile(profile: CustomerDTO) {
        if (customerRepository.existsByEmailIgnoreCase(profile.email.trim().lowercase())) {
            throw DuplicateEmailException()
        } else {
            val p = Customer()
            p.email = profile.email.trim().lowercase()
            p.name = profile.name
            p.surname = profile.surname
            p.address = profile.address
            customerRepository.save(p)
        }
    }

    @PreAuthorize("isAuthenticated() and ((#email == #principal.username and hasRole('Client')) or hasRole('Manager'))")
    override fun updateProfile(email: String, profile: CustomerDTO) {
        val p = customerRepository.findByEmailIgnoreCase(email.trim().lowercase())

        if (p != null) {
            p.name = profile.name
            p.surname = profile.surname
            customerRepository.save(p)
        } else {
            throw ProfileNotFoundException()
        }
    }

    override fun signup(credentials: CredentialCustomerDTO) {

        val user = UserRepresentation()
        user.username = credentials.customerDTO.email
        user.isEnabled = true
        user.isEmailVerified = true
        val role = keycloak.realm(realmName).roles().get("Client").toRepresentation().name
        user.realmRoles = listOf(role)

        val credentialsKeycloak = CredentialRepresentation()
        credentialsKeycloak.type = CredentialRepresentation.PASSWORD
        credentialsKeycloak.value = credentials.password
        credentialsKeycloak.isTemporary = false
        user.credentials = listOf(credentialsKeycloak)

        keycloak
            .realm(realmName)
            .users()
            .create(user)

    }
}