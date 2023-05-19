package it.polito.wa2.g19.server.profiles.staff

import it.polito.wa2.g19.server.profiles.DuplicateEmailException
import it.polito.wa2.g19.server.profiles.ProfileNotFoundException
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class StaffServiceImpl(
    private val staffRepository: StaffRepository
): StaffService {

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

}