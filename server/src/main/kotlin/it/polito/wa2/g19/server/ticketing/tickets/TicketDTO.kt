package it.polito.wa2.g19.server.ticketing.tickets

import it.polito.wa2.g19.server.profiles.Expert
import it.polito.wa2.g19.server.ticketing.statuses.PriorityLevel
import it.polito.wa2.g19.server.ticketing.statuses.PriorityLevelEnum
import it.polito.wa2.g19.server.ticketing.statuses.TicketStatus
import it.polito.wa2.g19.server.ticketing.statuses.TicketStatusEnum
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

open class TicketDTO(
    var id: Int? = null,
    @field:NotBlank(message = "customerEmail cannot be blank")
    var customerEmail: String,
    @field:NotBlank(message = "productEAN cannot be blank")
    var productEan: String,
    @field:NotBlank(message = "description cannot be blank")
    var description: String
)

class TicketOutDTO(id: Int?, customerEmail: String, productEan: String, description: String, priorityLevel: PriorityLevelEnum?, expertEmail: String?, status: TicketStatusEnum = TicketStatusEnum.Open)
    : TicketDTO(id, customerEmail, productEan, description){
    var priorityLevel: PriorityLevelEnum? = priorityLevel
    var expertEmail: String? = expertEmail
    var status: TicketStatusEnum = status
}

fun Ticket.toDTO() = TicketDTO(
    getId(),
    customer.email,
    product.ean,
    description)

fun Ticket.toOutDTO() :TicketOutDTO {

    val priorityLevel = if (this.priorityLevel != null){
        PriorityLevelEnum.valueOf(this.priorityLevel!!.name)
    } else{
        null
    }

    return TicketOutDTO(
        getId(),
        this.customer.email,
        this.product.ean,
        this.description,
        priorityLevel,
        this.expert?.email,
        this.status
    )

}