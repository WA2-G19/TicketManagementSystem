package it.polito.wa2.g19.server.profiles.staff

import it.polito.wa2.g19.server.profiles.DuplicateEmailException
import it.polito.wa2.g19.server.profiles.ProfileNotFoundException
import org.springframework.stereotype.Service

@Service
class StaffServiceImpl(
    private val staffRepository: StaffRepository
): StaffService {
    override fun getAll(): List<StaffDTO> {
        return staffRepository.findAll().map { it.toDTO() }
    }

    override fun getStaff(email: String): StaffDTO {
        val staff = staffRepository.findByEmailIgnoreCase(email.trim())
        if (staff == null) {
            throw ProfileNotFoundException()
        } else {
            return staff.toDTO()
        }
    }

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