package it.polito.wa2.g19.server.profiles

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ProfileServiceImpl(
    private val profileRepository: ProfileRepository
): ProfileService {
    override fun getAll(): List<ProfileDTO> {
        return profileRepository.findAll().map { it.toDTO() }
    }

    override fun getProfile(email: String): ProfileDTO? {

        val profile = profileRepository.findByIdOrNull(email.trim().lowercase())
        if(profile == null) {
            throw ProfileNotFoundException()
        } else {
            return profile.toDTO()
        }
    }

    override fun insertProfile(profile: ProfileDTO) {
        if (profileRepository.existsById(profile.email.trim().lowercase())) {
            throw DuplicateEmailException()
        } else {
            val p = Profile()
            p.email = profile.email.trim().lowercase()
            p.name = profile.name
            p.surname = profile.surname
            profileRepository.save(p)
        }
    }

    override fun updateProfile(email: String, profile: ProfileDTO) {
        val p = profileRepository.findByIdOrNull(email.trim().lowercase())

        if (p != null) {
            p.name = profile.name
            p.surname = profile.surname
            profileRepository.save(p)
        } else {
            throw ProfileNotFoundException()
        }
    }
}