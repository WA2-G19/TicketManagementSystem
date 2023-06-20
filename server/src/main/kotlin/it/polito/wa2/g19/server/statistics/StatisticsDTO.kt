package it.polito.wa2.g19.server.statistics

data class StatisticsDTO(
    val ticketsClosed: Int,
    val ticketsInProgress: Int,
    val averageTime: Float
)
