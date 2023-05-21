package it.polito.wa2.g19.server.ticketing.tickets

import it.polito.wa2.g19.server.common.Role
import it.polito.wa2.g19.server.common.Util
import it.polito.wa2.g19.server.ticketing.statuses.PriorityLevelEnum
import it.polito.wa2.g19.server.ticketing.statuses.TicketStatusDTO
import it.polito.wa2.g19.server.ticketing.statuses.TicketStatusEnum
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.net.URI

@RestController
@Validated
@RequestMapping("/API/tickets")
class TicketController(
    private val ticketService: TicketService,
    @Autowired
    @Qualifier("requestMappingHandlerMapping") private val handlerMapping: RequestMappingHandlerMapping
) {

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    fun getTickets(
        principal: JwtAuthenticationToken,
        @RequestParam(required = false) customer: String?,
        @RequestParam(required = false) expert: String?,
        @RequestParam(required = false) status: TicketStatusEnum?,
        @RequestParam(required = false) priorityLevel: PriorityLevelEnum?
    ): List<TicketOutDTO> {

        val role = Role.valueOf(principal.authorities.stream().findFirst().get().authority)
        val email = principal.name

        val tickets = when (role) {
            /*If the customer param is specified ignores it*/
            Role.ROLE_Client -> ticketService.getTickets(email, expert, status, priorityLevel)
            /*If the expert param is specified ignores it*/
            Role.ROLE_Expert -> ticketService.getTickets(customer, email, status, priorityLevel)

            Role.ROLE_Manager -> ticketService.getTickets(customer, expert, status, priorityLevel)
        }

        return tickets
    }

    @GetMapping("/{ticketId}")
    @ResponseStatus(HttpStatus.OK)
    fun getTicketById(
        principal: JwtAuthenticationToken,
        @PathVariable ticketId: Int
    ): TicketOutDTO {

        return ticketService.getTicket(ticketId)
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    fun postTicket(
        principal: JwtAuthenticationToken,
        @Valid
        @RequestBody
        ticket: TicketDTO
    ): ResponseEntity<Void> {
        val email = principal.name
        ticket.customerEmail = email
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
    @PutMapping("/{ticketId}")
    @ResponseStatus(HttpStatus.OK)
    fun putTicket(
        principal: JwtAuthenticationToken,
        @PathVariable
        ticketId: Int,
        @Valid
        @RequestBody
        ticketStatus: TicketStatusDTO
    ) {

        when(ticketStatus.status) {
            TicketStatusEnum.Reopened -> ticketService.reopenTicket(ticketId)
            TicketStatusEnum.InProgress -> ticketService.startProgressTicket(ticketId, principal.name, ticketStatus)
            TicketStatusEnum.Closed -> ticketService.closeTicket(ticketId, principal.name)
            TicketStatusEnum.Resolved -> ticketService.resolveTicket(ticketId, principal.name)
            else -> throw ForbiddenException()
        }
    }
}

