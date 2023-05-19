package it.polito.wa2.g19.server.profiles.customers

interface CustomerService {
    fun getAll(): List<CustomerDTO>
    fun getProfile(email: String): CustomerDTO
    fun insertProfile(profile: CustomerDTO)
    fun updateProfile(email: String, profile: CustomerDTO)
    fun signup(credentials: CredentialCustomerDTO)
}