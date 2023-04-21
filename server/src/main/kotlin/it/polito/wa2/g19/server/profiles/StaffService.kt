package it.polito.wa2.g19.server.profiles

interface StaffService {
    fun getAll(): List<StaffDTO>
    fun getProfile(email: String): StaffDTO
    fun insertProfile(profile: StaffDTO)
    fun updateProfile(email: String, profile: StaffDTO)
}