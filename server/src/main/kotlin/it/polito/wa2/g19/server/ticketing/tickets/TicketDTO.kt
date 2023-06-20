package it.polito.wa2.g19.server.ticketing.tickets

import it.polito.wa2.g19.server.ticketing.statuses.PriorityLevelEnum
import it.polito.wa2.g19.server.ticketing.statuses.TicketStatusEnum
import jakarta.validation.constraints.NotBlank
import java.util.UUID

open class TicketDTO(
    val id: Int? = null,

    val warrantyUUID: UUID,
    @field:NotBlank(message = "description cannot be blank")
    val description: String
)

class TicketOutDTO(id: Int?,
                   warrantyUUID: UUID,
                   description: String,

                   val customerEmail: String,
                   val productEan: String,
                   val priorityLevel: PriorityLevelEnum?,
                   val expertEmail: String?,
                   val status: TicketStatusEnum = TicketStatusEnum.Open

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