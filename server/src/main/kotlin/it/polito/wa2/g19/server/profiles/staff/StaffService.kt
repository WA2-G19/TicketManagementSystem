package it.polito.wa2.g19.server.profiles.staff

interface StaffService {
    fun getAll(): List<StaffDTO>
    fun getStaff(email: String): StaffDTO
    fun insertProfile(profile: StaffDTO)
    fun updateProfile(email: String, profile: StaffDTO)
}