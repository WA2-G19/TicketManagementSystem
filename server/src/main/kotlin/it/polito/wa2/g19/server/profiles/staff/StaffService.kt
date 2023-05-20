package it.polito.wa2.g19.server.profiles.staff

import it.polito.wa2.g19.server.profiles.customers.CredentialCustomerDTO

interface StaffService {
    fun getAll(): List<StaffDTO>
    fun getStaff(email: String): StaffDTO
    fun insertProfile(profile: StaffDTO)
    fun updateProfile(email: String, profile: StaffDTO)

    fun signupExpert(credential: CredentialStaffDTO)
}