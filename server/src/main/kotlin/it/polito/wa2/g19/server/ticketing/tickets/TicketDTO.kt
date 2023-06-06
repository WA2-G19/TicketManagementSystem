package it.polito.wa2.g19.server.ticketing.tickets

import it.polito.wa2.g19.server.ticketing.statuses.PriorityLevelEnum
import it.polito.wa2.g19.server.ticketing.statuses.TicketStatusEnum
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

class TicketOutDTO(id: Int?, customerEmail: String, productEan: String, description: String,
                   var priorityLevel: PriorityLevelEnum?,
                   var expertEmail: String?,
                   var status: TicketStatusEnum = TicketStatusEnum.Open
)
    : TicketDTO(id, customerEmail, productEan, description){
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