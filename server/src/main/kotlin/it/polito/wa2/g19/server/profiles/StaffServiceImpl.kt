package it.polito.wa2.g19.server.profiles

import org.springframework.data.repository.findByIdOrNull

class StaffServiceImpl(
    private val staffRepository: StaffRepository
): StaffService {
    override fun getAll(): List<StaffDTO> {
        return staffRepository.findAll().map { it.toDTO() }
    }

    override fun getProfile(email: String): StaffDTO {
        val profile = staffRepository.findByEmail(email.trim().lowercase())
        if (profile == null) {
            throw ProfileNotFoundException()
        } else {
            return profile.toDTO()
        }
    }

    override fun insertProfile(profile: StaffDTO) {
        if (staffRepository.existsByEmail(profile.email.trim().lowercase())) {
            throw DuplicateEmailException()
        } else {
            val p = if (profile.type == StaffType.Expert) {
                Expert()
            } else {
                Manager()
            }
            p.email = profile.email.trim().lowercase()
            p.name = profile.name
            p.surname = profile.surname
            staffRepository.save(p)
        }
    }

    override fun updateProfile(email: String, profile: StaffDTO) {
        val p = staffRepository.findByEmail(email.trim().lowercase())

        if (p != null) {
            p.name = profile.name
            p.surname = profile.surname
            staffRepository.save(p)
        } else {
            throw ProfileNotFoundException()
        }
    }

}