package it.polito.wa2.g19.server.ticketing.statuses

import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Validated
@RequestMapping("/API")
class TicketStatusController(
    private val ticketStatusService: TicketStatusService
) {

    // Only Manager
    @GetMapping("/stats/tickets-closed/{expertMail}")
    fun getTicketClosedByExpert(
        @PathVariable
        expertMail: String
    ): Int {
        return ticketStatusService.getTicketClosedByExpert(expertMail)
    }

    // Only Manager
    @GetMapping("/stats/average-time/{expertMail}")
    fun getAverageTimedByExpert(
        @PathVariable
        expertMail: String
    ): Float {
        return ticketStatusService.getAverageTimedByExpert(expertMail)
    }

}