package it.polito.wa2.g19.server.ticketing.tickets

import it.polito.wa2.g19.server.common.Util
import org.springframework.security.oauth2.jwt.Jwt
import it.polito.wa2.g19.server.ticketing.statuses.PriorityLevelEnum
import it.polito.wa2.g19.server.ticketing.statuses.TicketStatusDTO
import it.polito.wa2.g19.server.ticketing.statuses.TicketStatusEnum
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
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

    // Experts (If the ticket is assigned more than once, check the last status), Manager and Client but ONLY owns tickets
    @GetMapping("/tickets")
    @PreAuthorize("isAuthenticated()")
    fun getTickets(
        @RequestParam(required = false) customer: String?,
        @RequestParam(required = false) expert: String?,
        @RequestParam(required = false) status: TicketStatusEnum?,
        @RequestParam(required = false) priorityLevel: PriorityLevelEnum?
    ): List<TicketOutDTO> {
        val authToken = SecurityContextHolder.getContext().authentication.principal as Jwt
        val role = authToken.getClaim<List<String>>("role")[0]
        val tickets = ticketService.getTickets(customer, expert, status, priorityLevel)

        when (role) {
            "Client" -> {
                if (customer == authToken.getClaim("email"))
                    return tickets
                else
                    throw TicketNotFoundException()
            }
            "Expert" -> {
                if(expert == authToken.getClaim("email")) {
                    val list = mutableListOf<TicketOutDTO>()
                    tickets.forEach {
                        if(ticketService.getFinalStatus(it.id!!).ticket.expert?.email == expert) {
                            list.add(it)
                        }
                    }
                    return list.ifEmpty { throw TicketNotFoundException() }
                }
            }
            else -> {}
        }

        return tickets
    }

    // Manager All
    // Experts (If the ticket is assigned more than once, check the last status)
    // Client ONLY its tickets
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/tickets/{ticketId}")
    fun getTicketById(@PathVariable ticketId: Int): TicketOutDTO {
        val authToken = SecurityContextHolder.getContext().authentication.principal as Jwt
        val role = authToken.getClaim<List<String>>("role")[0]
        val ticket = ticketService.getTicket(ticketId)
        when (role) {
            "Client" -> {
                if (ticket.customerEmail == authToken.getClaim("email"))
                    return ticket
                else
                    throw TicketNotFoundException()
            }
            "Expert" -> {
                val status = ticketService.getFinalStatus(ticketId)
                if(status.ticket.expert?.email == authToken.getClaim("email")) {
                    return ticket
                } else {
                    throw TicketNotFoundException()
                }
            }
            else -> {}
        }

        return ticket
    }

    @PreAuthorize("hasRole('Client')")
    @PostMapping("/tickets")
    fun postTicket(
        @Valid
        @RequestBody
        ticket: TicketDTO
    ): ResponseEntity<Void> {
        val authToken = SecurityContextHolder.getContext().authentication.principal as Jwt
        ticket.customerEmail = authToken.getClaim("email")
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
    // Client (Just Reopened)
    // Manager (Can do Anything)
    // Expert (Progress To close, Progress to Open, Progress to Resolve)
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/tickets/{ticketId}")
    fun putTicket(
        @PathVariable
        ticketId: Int,
        @Valid
        @RequestBody
        ticketStatus: TicketStatusDTO
    ) {
        val authToken = SecurityContextHolder.getContext().authentication.principal as Jwt
        val email = authToken.getClaim<String>("email")
        val role = authToken.getClaim<List<String>>("role")[0]
        when (ticketStatus.status) {
            TicketStatusEnum.Reopened -> ticketService.reopenTicket(ticketId)
            TicketStatusEnum.InProgress -> {
                if (role != "Client" && role != "Expert")

                    ticketService.startProgressTicket(ticketId, email, ticketStatus)
            }

            TicketStatusEnum.Closed -> {
                if (role != "Client")
                    ticketService.closeTicket(ticketId, email)
            }

            TicketStatusEnum.Resolved -> {
                if (role != "Client")
                    ticketService.resolveTicket(ticketId, email)
            }

            else -> {}
        }
    }

}