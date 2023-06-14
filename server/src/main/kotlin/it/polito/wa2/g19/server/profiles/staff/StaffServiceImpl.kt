package it.polito.wa2.g19.server.profiles.staff

import it.polito.wa2.g19.server.profiles.DuplicateEmailException
import it.polito.wa2.g19.server.profiles.KeycloakException
import it.polito.wa2.g19.server.profiles.ProfileNotFoundException
import it.polito.wa2.g19.server.ticketing.tickets.ForbiddenException
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

@Service
@Transactional
class StaffServiceImpl(
    private val staffRepository: StaffRepository
) : StaffService {

    @Autowired
    private lateinit var keycloak: Keycloak

    @Value("\${keycloak.admin.realm}")
    private lateinit var realmName: String

    @PreAuthorize("hasRole('Manager')")
    override fun getAll(): List<StaffDTO> {
        return staffRepository.findAllExpert().map { it.toDTO() }
    }

    @PreAuthorize("isAuthenticated() and ((#email == #principal.username and hasRole('Expert')) or hasRole('Manager'))")
    override fun getStaff(email: String): StaffDTO {
        val staff = staffRepository.findByEmailIgnoreCase(email.trim())
        if (staff == null) {
            throw ProfileNotFoundException()
        } else {
            return staff.toDTO()
        }
    }

    @PreAuthorize("hasRole('Manager')")
    override fun insertProfile(profile: StaffDTO) {
        if (staffRepository.existsByEmailIgnoreCase(profile.email.trim())) {
            throw DuplicateEmailException()
        } else {
            val p = if (profile.type == StaffType.Expert) {
                Expert()
            } else {
                Manager()
            }
            p.email = profile.email.trim()
            p.name = profile.name
            p.surname = profile.surname
            staffRepository.save(p)
        }
    }

    @PreAuthorize("isAuthenticated() and ((#email == #principal.username and hasRole('Expert')) or hasRole('Manager'))")
    override fun updateProfile(email: String, profile: StaffDTO) {
        val p = staffRepository.findByEmailIgnoreCase(email.trim().lowercase())

        if (p != null) {
            p.name = profile.name
            p.surname = profile.surname
            staffRepository.save(p)
        } else {
            throw ProfileNotFoundException()
        }
    }

    @PreAuthorize("hasRole('Manager')")
    override fun createExpert(credential: CredentialStaffDTO) {
        credential.staffDTO.email = credential.staffDTO.email.lowercase()
        val user = UserRepresentation()
        user.username = credential.staffDTO.email
        user.email = credential.staffDTO.email
        user.firstName = credential.staffDTO.name
        user.lastName = credential.staffDTO.surname

        if (credential.staffDTO.type != StaffType.Expert) throw ForbiddenException()

        user.isEnabled = true
        user.isEmailVerified = true

        val credentialsKeycloak = CredentialRepresentation()
        credentialsKeycloak.type = CredentialRepresentation.PASSWORD
        credentialsKeycloak.value = credential.password
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
        val role = keycloak.realm(realmName).roles().get("Expert").toRepresentation()
        val userId = CreatedResponseUtil.getCreatedId(response)
        val userResponse = userResource.get(userId)
        userResponse.roles().realmLevel().add(listOf(role))

        // Insert also inside the Database
        try{
            val profile = credential.staffDTO
            val p = Expert().apply {
                id = UUID.fromString(userId)
                email = profile.email.trim()
                name = profile.name
                surname = profile.surname
            }
            staffRepository.save(p)
        } catch (e: Exception){
            //delete the expert is something go wrong

            userResource.get(userId).remove()
            throw KeycloakException()
        }

    }
}