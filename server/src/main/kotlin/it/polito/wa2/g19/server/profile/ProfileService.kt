package it.polito.wa2.g19.server.profile

interface ProfileService {
    fun getAll(): List<ProfileDTO>
    fun getProfile(email: String): ProfileDTO?
    fun insertProfile(profile: ProfileDTO)
    fun updateProfile(profile: ProfileDTO)
}