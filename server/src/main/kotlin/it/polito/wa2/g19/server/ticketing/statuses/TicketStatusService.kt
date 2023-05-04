package it.polito.wa2.g19.server.ticketing.statuses

interface TicketStatusService {



    fun getTicketClosedByExpert(expertMail: String): Int

    fun getAverageTimedByExpert(expertMail: String): Float

}