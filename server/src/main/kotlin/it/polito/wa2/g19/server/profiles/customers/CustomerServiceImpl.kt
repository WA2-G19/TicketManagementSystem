package it.polito.wa2.g19.server.profiles.customers

import it.polito.wa2.g19.server.profiles.DuplicateEmailException
import it.polito.wa2.g19.server.profiles.KeycloakException
import it.polito.wa2.g19.server.profiles.ProfileNotFoundException
import org.apache.http.HttpStatus
import org.keycloak.admin.client.CreatedResponseUtil
import org.keycloak.admin.client.Keycloak
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.collections.HashMap


@Service
@Transactional("transactionManager")
class CustomerServiceImpl(
    private val customerRepository: CustomerRepository
) : CustomerService {

    @Autowired
    private lateinit var keycloak: Keycloak

    @Value("\${keycloak.admin.realm}")
    private lateinit var realmName: String

    @PreAuthorize("hasRole('Manager')")
    override fun getAll(): List<CustomerDTO> {
        return customerRepository.findAll().map { it.toDTO() }
    }

    @PreAuthorize("isAuthenticated() and (hasRole('Manager') or #email == principal.claims['email'])")
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

    @PreAuthorize("isAuthenticated() and ((#email == principal.claims['email'] and hasRole('Client')) or hasRole('Manager'))")
    override fun updateProfile(email: String, profile: CustomerDTO) {
        val p = customerRepository.findByEmailIgnoreCase(email.trim().lowercase()) ?: throw ProfileNotFoundException()
        p.address = profile.address
        customerRepository.save(p)
    }

    override fun signup(credentials: CredentialCustomerDTO) {
        val user = UserRepresentation()
        credentials.customerDTO.email = credentials.customerDTO.email.lowercase()
        user.username = credentials.customerDTO.email
        user.email = credentials.customerDTO.email
        user.firstName = credentials.customerDTO.name
        user.lastName = credentials.customerDTO.surname
        user.attributes = HashMap()
        user.attributes["address"] = listOf(credentials.customerDTO.address)

        user.isEnabled = true
        user.isEmailVerified = true

        val credentialsKeycloak = CredentialRepresentation()
        credentialsKeycloak.type = CredentialRepresentation.PASSWORD
        credentialsKeycloak.value = credentials.password

        credentialsKeycloak.isTemporary = false
        user.credentials = listOf(credentialsKeycloak)

        val userResource = keycloak
            .realm(realmName)
            .users()


        // Check if the user already exists
        val response = userResource.create(user)
        if (response.status == HttpStatus.SC_CONFLICT) throw DuplicateEmailException()
        if (response.status != 201 ) throw KeycloakException()

        // Assign the role to client
        val role = keycloak.realm(realmName).roles().get("Client").toRepresentation()
        val userId = CreatedResponseUtil.getCreatedId(response)
        val userResponse = userResource.get(userId)
        userResponse.roles().realmLevel().add(listOf(role))
        try{
            val p = Customer().apply {
                id = UUID.fromString(userId)
                email = credentials.customerDTO.email.trim().lowercase()
                name = credentials.customerDTO.name
                surname = credentials.customerDTO.surname
                address = credentials.customerDTO.address
            }
            customerRepository.save(p)
//            userResource.get(userId).sendVerifyEmail()
        } catch (e: Exception){
            //delete the user is something go wrong
            userResource.get(userId).remove()
            throw KeycloakException()
        }
        // Insert inside database


    }
}