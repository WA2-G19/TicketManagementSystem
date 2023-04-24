package it.polito.wa2.g19.server.tickets

import jakarta.validation.Valid
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
    fun postTicket(@Valid ticket: TicketDTO) {
        ticketService.createTicket(ticket)
    }
}