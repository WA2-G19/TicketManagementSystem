package it.polito.wa2.g19.server.ticketing.tickets

import it.polito.wa2.g19.server.common.Util
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
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.net.URI

@RestController
@Validated
@RequestMapping("/API")
class TicketController(
    private val ticketService: TicketService,
    private val handlerMapping: RequestMappingHandlerMapping
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
        headers.location = URI.create(Util.getUri(handlerMapping, ::getTicketById.name, id))
        return ResponseEntity(null, headers, HttpStatus.CREATED)
    }


    /*
       Since a PUT method should ensure the idempotency property, actually this should be a PATCH method,
       however, the RestTemplate object used for testing does not allow us to issue PATCH request.
       Just for the sake of testing we use treat this method as it handle a PUT method.
    */
    @PutMapping("/tickets/{ticketId}")
    fun putTicket(
        @PathVariable
        ticketId: Int,
        @Valid
        @RequestBody
        ticketStatus: TicketStatusDTO
    ){
        when(ticketStatus.status){
            TicketStatusEnum.Reopened -> ticketService.reopenTicket(ticketId)
            TicketStatusEnum.InProgress -> ticketService.startProgressTicket(ticketId, ticketStatus.by!!, ticketStatus)
            TicketStatusEnum.Closed -> ticketService.closeTicket(ticketId, ticketStatus.by!!)
            TicketStatusEnum.Resolved -> ticketService.resolveTicket(ticketId, ticketStatus.by!!)
            else -> {}
        }
    }

}