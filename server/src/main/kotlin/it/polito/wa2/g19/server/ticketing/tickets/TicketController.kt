package it.polito.wa2.g19.server.ticketing.tickets

import io.micrometer.observation.annotation.Observed
import it.polito.wa2.g19.server.common.Role
import it.polito.wa2.g19.server.common.Util
import it.polito.wa2.g19.server.ticketing.statuses.PriorityLevelEnum
import it.polito.wa2.g19.server.ticketing.statuses.TicketStatusDTO
import it.polito.wa2.g19.server.ticketing.statuses.TicketStatusEnum
import jakarta.validation.Valid
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.net.URI


@RestController
@CrossOrigin
@Validated
@RequestMapping("/API/tickets")
@Observed
@Slf4j
class TicketController(
    private val ticketService: TicketService,
    @Autowired
    @Qualifier("requestMappingHandlerMapping") private val handlerMapping: RequestMappingHandlerMapping
) {

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    fun getTickets(
        @RequestParam(required = false) customer: String?,
        @RequestParam(required = false) expert: String?,
        @RequestParam(required = false) status: TicketStatusEnum?,
        @RequestParam(required = false) priorityLevel: PriorityLevelEnum?
    ): List<TicketOutDTO> {
        val principal = SecurityContextHolder.getContext().authentication
        val email = principal.name

        return if (principal.authorities.any { it.authority == Role.ROLE_Expert.name }) {
            ticketService.getTickets(customer, email, status, priorityLevel)
        } else if (principal.authorities.any { it.authority == Role.ROLE_Client.name }) {
            ticketService.getTickets(email, expert, status, priorityLevel)
        } else if (principal.authorities.any { it.authority == Role.ROLE_Manager.name }) {
            ticketService.getTickets(customer, expert, status, priorityLevel)
        } else {
            throw ForbiddenException()
        }
    }

    @GetMapping("/{ticketId}")
    @ResponseStatus(HttpStatus.OK)
    fun getTicketById(
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

