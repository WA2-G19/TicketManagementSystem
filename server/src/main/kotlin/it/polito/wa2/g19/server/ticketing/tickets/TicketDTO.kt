package it.polito.wa2.g19.server.ticketing.tickets

import it.polito.wa2.g19.server.ticketing.statuses.PriorityLevelEnum
import it.polito.wa2.g19.server.ticketing.statuses.TicketStatusEnum
import jakarta.validation.constraints.NotBlank
import java.util.UUID

open class TicketDTO(
    var id: Int? = null,

    var warrantyUUID: UUID,
    @field:NotBlank(message = "description cannot be blank")
    var description: String
)

class TicketOutDTO(id: Int?,
                   warrantyUUID: UUID,
                   description: String,

                   var customerEmail: String,
                   var productEan: String,
                   var priorityLevel: PriorityLevelEnum?,
                   var expertEmail: String?,
                   var status: TicketStatusEnum = TicketStatusEnum.Open
)
    : TicketDTO(id, warrantyUUID, description){
}

fun Ticket.toDTO() = TicketDTO(
    getId(),
    warranty.id!!,
    description)

fun Ticket.toOutDTO() :TicketOutDTO {

    val priorityLevel = if (this.priorityLevel != null){
        PriorityLevelEnum.valueOf(this.priorityLevel!!.name)
    } else{
        null
    }

    return TicketOutDTO(
        getId(),
        this.warranty.id!!,
        this.description,
        this.warranty.customer!!.email,
        this.warranty.product.ean,
        priorityLevel,
        this.expert?.email,
        this.status
    )

}