package it.polito.wa2.g19.server.tickets.statuses

import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

data class TicketStatusDTO(
    val ticketId: Int,
    @field:Enumerated(EnumType.STRING)
    val status: TicketStatusEnum,
    val expert: String?,
    val by: String?,
    val priorityLevel: String?,
    val timestamp: LocalDateTime?
) {
    @AssertTrue
    fun isValid(): Boolean = when (status) {
        TicketStatusEnum.Open -> expert.isNullOrEmpty() && by.isNullOrEmpty() && priorityLevel.isNullOrEmpty()
        TicketStatusEnum.InProgress -> !expert.isNullOrEmpty() && !by.isNullOrEmpty() && !priorityLevel.isNullOrEmpty()
        TicketStatusEnum.Closed -> expert.isNullOrEmpty() && !by.isNullOrEmpty() && priorityLevel.isNullOrEmpty()
        TicketStatusEnum.Resolved -> expert.isNullOrEmpty() && !by.isNullOrEmpty() && priorityLevel.isNullOrEmpty()
        TicketStatusEnum.Reopened -> expert.isNullOrEmpty() && by.isNullOrEmpty() && priorityLevel.isNullOrEmpty()
    }
}

enum class TicketStatusEnum {
    Open,
    InProgress,
    Closed,
    Resolved,
    Reopened
}