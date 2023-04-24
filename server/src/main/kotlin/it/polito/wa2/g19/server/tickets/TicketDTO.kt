package it.polito.wa2.g19.server.tickets

import jakarta.validation.constraints.NotBlank

data class TicketDTO(
    var id: Int? = null,
    @field:NotBlank(message = "customerEmail cannot be blank")
    var customerEmail: String,
    @field:NotBlank(message = "productEAN cannot be blank")
    var productEan: String,
    @field:NotBlank(message = "description cannot be blank")
    var description: String
)

fun Ticket.toDTO() = TicketDTO(
    getId(),
    customer.email,
    product.ean,
    description)