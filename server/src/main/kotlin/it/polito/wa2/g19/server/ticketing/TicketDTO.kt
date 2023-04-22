package it.polito.wa2.g19.server.ticketing

import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.validation.constraints.NotBlank

data class TicketDTO(
    var id: Int? = null,
    var customerId: Int,
    var productId: Int,
    @field:NotBlank(message = "description cannot be blank")
    var description: String,
    @field:Enumerated(EnumType.STRING)
    var status: TicketDTOStatus,
    @field:NotBlank(message = "priority cannot be blank")
    var priority: String?
)

enum class TicketDTOStatus {
    Open,
    InProgress,
    Closed,
    Resolved,
    Reopened
}

fun Ticket.toDTO() = TicketDTO(
    getId(),
    customer.getId()!!,
    product.getId()!!,
    description,
    status.toDTO(),
    priorityLevel?.name)