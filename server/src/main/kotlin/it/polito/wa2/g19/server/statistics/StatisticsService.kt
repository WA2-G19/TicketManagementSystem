package it.polito.wa2.g19.server.statistics

interface StatisticsService {
    fun getTicketClosedByExpert(expertMail: String): Int
    fun getAverageTimedByExpert(expertMail: String): Float
    fun getTicketsInProgressByExpert(expertMail: String): Int
}