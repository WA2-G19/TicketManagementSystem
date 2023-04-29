package it.polito.wa2.g19.server.ticketing.tickets

import it.polito.wa2.g19.server.ticketing.statuses.PriorityLevelEnum
import it.polito.wa2.g19.server.ticketing.statuses.TicketStatusDTO
import it.polito.wa2.g19.server.ticketing.statuses.TicketStatusEnum
import it.polito.wa2.g19.server.ticketing.statuses.TicketStatusService
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@Validated
@RequestMapping("/API")
class TicketController(
    private val ticketService: TicketService,
    private val ticketStatusService: TicketStatusService
) {

    @GetMapping("/tickets")
    fun getTickets(
        @RequestParam(required = false) customer: String?,
        @RequestParam(required = false) expert: String?,
        @RequestParam(required = false) status: TicketStatusEnum?,
        @RequestParam(required = false) priorityLevel: PriorityLevelEnum?
    ): List<TicketOutDTO> {
        return ticketService.getTickets(customer, expert, status, priorityLevel)
    }

    @GetMapping("/tickets/{ticketId}")
    fun getTicketById(@PathVariable ticketId: Int, ): TicketOutDTO {

        return ticketService.getTicket(ticketId)
    }

    @PostMapping("/tickets")
    fun postTicket(@Valid
                   @RequestBody
                   ticket: TicketDTO
    ): ResponseEntity<Void> {
        val id = ticketService.createTicket(ticket)
        val headers = HttpHeaders()
        headers.location = URI.create("/API/tickets/${id}")
        return ResponseEntity(null, headers, HttpStatus.CREATED)
    }

    @PutMapping("/tickets/{ticketId}")
    fun putTicket(
        @PathVariable
        ticketId: Int,
        @Valid
        @RequestBody
        ticketStatus: TicketStatusDTO
    ){
        when(ticketStatus.status){
            TicketStatusEnum.Reopened -> ticketStatusService.reopenTicket(ticketId)
            TicketStatusEnum.InProgress -> ticketStatusService.startProgressTicket(ticketId, ticketStatus.by!!, ticketStatus)
            TicketStatusEnum.Closed -> ticketStatusService.closeTicket(ticketId, ticketStatus.by!!)
            TicketStatusEnum.Resolved -> ticketStatusService.resolveTicket(ticketId, ticketStatus.by!!)
            else -> {}
        }
    }
}