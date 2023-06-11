package it.polito.wa2.g19.server.ticketing.statuses

import io.micrometer.observation.annotation.Observed
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@Validated
@CrossOrigin
@RequestMapping("/API/stats")
@Observed
class TicketStatusController(
    private val ticketStatusService: TicketStatusService
) {

    @GetMapping("/tickets-closed/{expertMail}")
    fun getTicketClosedByExpert(
        @PathVariable
        expertMail: String
    ): Int {
        return ticketStatusService.getTicketClosedByExpert(expertMail)
    }

    @GetMapping("/average-time/{expertMail}")
    fun getAverageTimedByExpert(
        @PathVariable
        expertMail: String
    ): Float {
        return ticketStatusService.getAverageTimedByExpert(expertMail)
    }

}