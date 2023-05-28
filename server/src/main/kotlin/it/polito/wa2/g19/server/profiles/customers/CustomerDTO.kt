package it.polito.wa2.g19.server.profiles.customers

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.hibernate.annotations.Check

data class CustomerDTO(
    @field:Email(message = "provide a valid email", )
    @field:NotBlank(message = "email cannot be blank")
    var email: String,
    @field:NotBlank(message = "name cannot be blank" )
    val name: String,
    @field:NotBlank(message = "surname cannot be blank")
    val surname: String,
    @field:NotBlank(message = "address cannot be blank")
    val address: String
)

data class CredentialCustomerDTO (

    val customerDTO: CustomerDTO,

    @field:NotBlank(message = "password cannot be blank")
    @field:Size(min = 6, message = "password size should be minimum 6 characters")
    val password: String


)

fun Customer.toDTO(): CustomerDTO = CustomerDTO(email.trim().lowercase(), name, surname, address)