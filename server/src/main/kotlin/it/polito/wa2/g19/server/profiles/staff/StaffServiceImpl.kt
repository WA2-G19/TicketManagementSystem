package it.polito.wa2.g19.server.profiles.staff

import it.polito.wa2.g19.server.profiles.DuplicateEmailException
import it.polito.wa2.g19.server.profiles.ProfileAlreadyPresent
import it.polito.wa2.g19.server.profiles.ProfileNotFoundException
import it.polito.wa2.g19.server.profiles.customers.CredentialCustomerDTO
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
import org.springframework.web.bind.annotation.ResponseStatus

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
        return staffRepository.findAll().map { it.toDTO() }
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
    @ResponseStatus(org.springframework.http.HttpStatus.CREATED)
    override fun signupExpert(credentials: CredentialStaffDTO) {

        val user = UserRepresentation()
        user.username = credentials.staffDTO.email
        user.email = credentials.staffDTO.email
        user.firstName = credentials.staffDTO.name
        user.lastName = credentials.staffDTO.surname

        if (credentials.staffDTO.type != StaffType.Expert) throw ForbiddenException()

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

        // Assign the role to client
        val role = keycloak.realm(realmName).roles().get("Expert").toRepresentation()
        val userId = CreatedResponseUtil.getCreatedId(response)
        val userResponse = userResource.get(userId)
        userResponse.roles().realmLevel().add(listOf(role))

        // Insert also inside the Database
        val profile = credentials.staffDTO
        val p = Expert()
        p.email = profile.email.trim()
        p.name = profile.name
        p.surname = profile.surname
        staffRepository.save(p)


    }


}