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
        val profile = profileRepository.findByIdOrNull(email)
        if(profile == null) {
            throw ProfileNotFoundException("There is no profile associated to this email")
        } else {
            return profile.toDTO()
        }
    }

    override fun insertProfile(profile: ProfileDTO) {
        if (profileRepository.existsById(profile.email)) {
            throw DuplicateEmailException("There is already an email associated to this profile")
        } else {
            val p = Profile()
            p.email = profile.email
            p.name = profile.name
            p.surname = profile.surname
            profileRepository.save(p)
        }
    }

    override fun updateProfile(email: String, profile: ProfileDTO) {
        val p = profileRepository.findByIdOrNull(email)

        if (p != null) {
            p.name = profile.name
            p.surname = profile.surname
            profileRepository.save(p)
        } else {
            throw ProfileNotFoundException("This profile does not exist")
        }
    }
}