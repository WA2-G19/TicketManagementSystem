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
    @GetMapping("/ticket/{ticketId}/statusHistory")
    fun getStatusHistory(@PathVariable ticketId: Int): Set<TicketStatusDTO> {
        return ticketStatusService.getStatusHistory(ticketId)
    }

    @GetMapping("/ticket/{ticketId}/status")
    fun getCurrentStatus(@PathVariable ticketId: Int): TicketStatusDTO {
        return ticketStatusService.getCurrentStatus(ticketId)
    }

    @PostMapping("/ticket/{ticketId}/stopProgress")
    fun postOpenTicket(@PathVariable ticketId: Int) {
        ticketStatusService.stopProgressTicket(ticketId)
    }

    @PostMapping("/ticket/{ticketId}/startProgress")
    fun postInProgressTicket(@PathVariable ticketId: Int, @RequestBody @Valid status: TicketStatusDTO) {
        ticketStatusService.startProgressTicket(ticketId, status.expert!!, status!!)
    }

    @PostMapping("/tickets/{ticketId}/reopen")
    fun postCloseTicket(@PathVariable ticketId: Int, @RequestBody @Valid status: TicketStatusDTO) {
        ticketStatusService.closeTicket(ticketId, status.by!!)
    }

    @PostMapping("/ticket/{ticketId}/resolve")
    fun postResolveTicket(@PathVariable ticketId: Int, @RequestBody @Valid status: TicketStatusDTO) {
        ticketStatusService.resolveTicket(ticketId, status.by!!)
    }

    @PostMapping("/ticket/{ticketId}/reopen")
    fun postReopenTicket(@PathVariable ticketId: Int) {
        ticketStatusService.reopenTicket(ticketId)
    }

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