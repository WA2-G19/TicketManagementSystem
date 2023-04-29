package it.polito.wa2.g19.server.ticketing.statuses

import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.validation.constraints.AssertTrue
import java.time.LocalDateTime

data class TicketStatusDTO(
    val ticketId: Int,
    @field:Enumerated(EnumType.STRING)
    val status: TicketStatusEnum,
    val expert: String? = null,
    val by: String? = null,
    val priorityLevel: PriorityLevelEnum? = null,
    val timestamp: LocalDateTime? = null
) {
    @AssertTrue
    fun isValid(): Boolean = when (status) {
        TicketStatusEnum.Open -> expert.isNullOrEmpty() && by.isNullOrEmpty() && priorityLevel == null
        TicketStatusEnum.InProgress -> !expert.isNullOrEmpty() && !by.isNullOrEmpty() && priorityLevel != null
        TicketStatusEnum.Closed -> expert.isNullOrEmpty() && !by.isNullOrEmpty() && priorityLevel == null
        TicketStatusEnum.Resolved -> expert.isNullOrEmpty() && !by.isNullOrEmpty() && priorityLevel == null
        TicketStatusEnum.Reopened -> expert.isNullOrEmpty() && by.isNullOrEmpty() && priorityLevel == null
    }
}

enum class TicketStatusEnum {
    Open,
    InProgress,
    Closed,
    Resolved,
    Reopened
}