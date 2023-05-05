package it.polito.wa2.g19.server.profiles.customers

import it.polito.wa2.g19.server.profiles.DuplicateEmailException
import it.polito.wa2.g19.server.profiles.ProfileNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CustomerServiceImpl(
    private val customerRepository: CustomerRepository
): CustomerService {
    override fun getAll(): List<CustomerDTO> {
        return customerRepository.findAll().map { it.toDTO() }
    }

    override fun getProfile(email: String): CustomerDTO {

        val profile = customerRepository.findByEmailIgnoreCase(email.trim().lowercase())
        if (profile == null) {
            throw ProfileNotFoundException()
        } else {
            return profile.toDTO()
        }
    }

    override fun insertProfile(profile: CustomerDTO) {
        if (customerRepository.existsByEmailIgnoreCase(profile.email.trim().lowercase())) {
            throw DuplicateEmailException()
        } else {
            val p = Customer()
            p.email = profile.email.trim().lowercase()
            p.name = profile.name
            p.surname = profile.surname
            p.address = profile.address
            customerRepository.save(p)
        }
    }

    override fun updateProfile(email: String, profile: CustomerDTO) {
        val p = customerRepository.findByEmailIgnoreCase(email.trim().lowercase())

        if (p != null) {
            p.name = profile.name
            p.surname = profile.surname
            customerRepository.save(p)
        } else {
            throw ProfileNotFoundException()
        }
    }
}