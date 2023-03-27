package it.polito.wa2.g19.server.profile

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
        return profileRepository.findByIdOrNull(email)?.toDTO()
    }

    override fun insertProfile(profile: ProfileDTO) {
        if (profileRepository.existsById(profile.email)) {
            throw NotFoundException()
        } else {
            val p = Profile()
            p.email = profile.email
            p.name = profile.name
            p.surname = profile.surname
            profileRepository.save(p)
        }
    }

    override fun updateProfile(profile: ProfileDTO) {
        val p = profileRepository.findByIdOrNull(profile.email)
        if (p != null) {
            p.name = profile.name
            p.surname = profile.surname
            profileRepository.save(p)
        } else {
            throw NotFoundException()
        }
    }
}