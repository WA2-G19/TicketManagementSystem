package it.polito.wa2.g19.server.ticketing.tickets

import jakarta.validation.Valid
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.net.http.HttpResponse

@RestController
@Validated
@RequestMapping("/API")
class TicketController(
    private val ticketService: TicketService
) {

    @GetMapping("/tickets")
    fun getTickets(): Set<TicketDTO> {
        return ticketService.getTickets()
    }

    @GetMapping("/tickets/{ticketId}")
    fun getTicketById(@PathVariable ticketId: Int): TicketDTO {
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
}