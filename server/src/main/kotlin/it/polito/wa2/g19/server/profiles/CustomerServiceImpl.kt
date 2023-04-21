package it.polito.wa2.g19.server.profiles

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class CustomerServiceImpl(
    private val customerRepository: CustomerRepository
): CustomerService {
    override fun getAll(): List<CustomerDTO> {
        return customerRepository.findAll().map { it.toDTO() }
    }

    override fun getProfile(email: String): CustomerDTO {
        val profile = customerRepository.findByIdOrNull(email.trim().lowercase())
        if (profile == null) {
            throw ProfileNotFoundException()
        } else {
            return profile.toDTO()
        }
    }

    override fun insertProfile(profile: CustomerDTO) {
        if (customerRepository.existsById(profile.email.trim().lowercase())) {
            throw DuplicateEmailException()
        } else {
            val p = Customer()
            p.email = profile.email.trim().lowercase()
            p.name = profile.name
            p.surname = profile.surname
            customerRepository.save(p)
        }
    }

    override fun updateProfile(email: String, profile: CustomerDTO) {
        val p = customerRepository.findByIdOrNull(email.trim().lowercase())

        if (p != null) {
            p.name = profile.name
            p.surname = profile.surname
            customerRepository.save(p)
        } else {
            throw ProfileNotFoundException()
        }
    }
}