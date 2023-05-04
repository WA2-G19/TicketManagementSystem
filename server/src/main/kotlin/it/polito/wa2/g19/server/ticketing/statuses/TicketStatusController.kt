package it.polito.wa2.g19.server.ticketing.statuses

import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@RestController
@Validated
@RequestMapping("/API")
class TicketStatusController(
    private val ticketStatusService: TicketStatusService
) {


    @GetMapping("/stats/ticketsClosed/{expertMail}")
    fun getTicketClosedByExpert(
        @PathVariable
        expertMail: String
    ): Int {
        return ticketStatusService.getTicketClosedByExpert(expertMail)
    }

    @GetMapping("/stats/averageTime/{expertMail}")
    fun getAverageTimedByExpert(
        @PathVariable
        expertMail: String
    ): Float {
        return ticketStatusService.getAverageTimedByExpert(expertMail)
    }

}