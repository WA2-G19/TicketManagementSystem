package it.polito.wa2.g19.server.ticketing.tickets

import it.polito.wa2.g19.server.common.Role
import it.polito.wa2.g19.server.products.ProductNotFoundException
import it.polito.wa2.g19.server.products.ProductRepository
import it.polito.wa2.g19.server.profiles.customers.CustomerRepository
import it.polito.wa2.g19.server.profiles.ProfileNotFoundException
import it.polito.wa2.g19.server.profiles.staff.Expert
import it.polito.wa2.g19.server.profiles.staff.Manager
import it.polito.wa2.g19.server.profiles.staff.StaffRepository
import it.polito.wa2.g19.server.ticketing.statuses.*
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class TicketServiceImpl(
    private val ticketRepository: TicketRepository,
    private val customerRepository: CustomerRepository,
    private val productRepository: ProductRepository,
    private val staffRepository: StaffRepository,
    private val priorityLevelRepository: PriorityLevelRepository,
    private val ticketStatusRepository: TicketStatusRepository
): TicketService {

    override fun getTicket(id: Int, principal: JwtAuthenticationToken): TicketOutDTO {
        val role = Role.valueOf(principal.authorities.stream().findFirst().get().authority)
        val email = principal.name

        val ticket =
            when(role){
                Role.ROLE_Client -> ticketRepository.findTicketByIdAndCustomerEmail(id, email)
                Role.ROLE_Expert -> ticketRepository.findTicketByIdAndExpertEmail(id, email)
                Role.ROLE_Manager -> ticketRepository.findByIdOrNull(id)
            } ?: throw TicketNotFoundException()

        return ticket.toOutDTO()
    }

    override fun getTickets(
        customerEmail: String?,
        expertEmail: String?,
        statusEnum: TicketStatusEnum?,
        priorityLevel: PriorityLevelEnum?
    ): List<TicketOutDTO> {



        val expert = if (expertEmail != null) {
            staffRepository.findByEmailIgnoreCase(expertEmail)
        } else
            null
        val customer = if (customerEmail != null) {
            customerRepository.findByEmailIgnoreCase(customerEmail)
        } else
            null

        val priorityLevelVal = if (priorityLevel != null) {
            priorityLevelRepository.findByName(priorityLevel.name)
        } else null



        return ticketRepository.findAll(
            (TicketSpecification.ofCustomer(customer).and(TicketSpecification.ofExpert(expert))
                .and(
                    TicketSpecification.ofStatus(statusEnum)
                        .and(TicketSpecification.ofPriority(priorityLevelVal))
                ))
        ).map { it.toOutDTO() }

    }


    override fun createTicket(ticket: TicketDTO): Int {
        val c = customerRepository.findByEmailIgnoreCase(ticket.customerEmail) ?: throw ProfileNotFoundException()
        val p = productRepository.findByEan(ticket.productEan) ?: throw ProductNotFoundException()
        val t = Ticket().apply {
            customer = c
            product = p
            description = ticket.description
            statusHistory = mutableSetOf()
            status = TicketStatusEnum.Open
            expert = null
            priorityLevel = null

        }
        t.statusHistory.add(OpenTicketStatus().apply {
            this.ticket = t
            this.timestamp = LocalDateTime.now()
        })
        val ticketCreated = ticketRepository.save(t)
        return ticketCreated.getId()!!
    }

    //SOLO MANAGER O EXPERT (SE EXPERT BISOGNA VERIFICARE CHE IL TICKET SIA SUO)

    override fun stopProgressTicket(ticketId: Int) {
        val ticket: Ticket = ticketRepository.findByIdOrNull(ticketId) ?: throw TicketNotFoundException()

        val current = ticketStatusRepository.findByTicketAndTimestampIsMaximum(ticketId)
        if (current is InProgressTicketStatus) {
            ticketStatusRepository.save(OpenTicketStatus().apply {
                this.ticket = ticket
                timestamp = LocalDateTime.now()
            })
        } else {
            throw InvalidTicketStatusTransitionException(current.toDTO().status, TicketStatusEnum.Open)
        }
    }

    @PreAuthorize("hasRole('Client')")
    override fun reopenTicket(ticketId: Int) {
        val client = SecurityContextHolder.getContext().authentication as JwtAuthenticationToken;
        val ticket: Ticket = ticketRepository.findTicketByIdAndCustomerEmail(ticketId, client.name) ?: throw TicketNotFoundException()
        if (ticket.status == TicketStatusEnum.Closed || ticket.status == TicketStatusEnum.Resolved) {
            ticket.status = TicketStatusEnum.Reopened
            ticket.statusHistory.add(ReopenedTicketStatus().apply {
                this.ticket = ticket
                timestamp = LocalDateTime.now()
            })
            ticketRepository.save(ticket)
        } else {
            throw InvalidTicketStatusTransitionException(ticket.toOutDTO().status, TicketStatusEnum.Reopened)
        }
    }

    @PreAuthorize("hasRole('Manager')")
    override fun startProgressTicket(ticketId: Int, managerEmail: String, ticketStatus: TicketStatusDTO) {
        val ticket: Ticket = ticketRepository.findByIdOrNull(ticketId) ?: throw TicketNotFoundException()
        val expert = staffRepository.findByEmailIgnoreCase(ticketStatus.expert!!) ?: throw ProfileNotFoundException()
        if (expert !is Expert) {
            throw ProfileNotFoundException()
        }
        val manager = staffRepository.findByEmailIgnoreCase(managerEmail) ?: throw ProfileNotFoundException()
        if (manager !is Manager) {
            throw ProfileNotFoundException()
        }
        if (ticket.status == TicketStatusEnum.Open || ticket.status == TicketStatusEnum.Reopened) {
            ticket.expert = expert
            ticket.status = TicketStatusEnum.InProgress
            ticket.priorityLevel = priorityLevelRepository.findByName(ticketStatus.priorityLevel!!.name)
            ticket.statusHistory.add(InProgressTicketStatus().apply {
                this.ticket = ticket
                this.expert = expert
                by = manager
                timestamp = LocalDateTime.now()
                priority = ticket.priorityLevel!!
            })
            ticketRepository.save(ticket)

        } else {
            throw InvalidTicketStatusTransitionException(ticket.status, TicketStatusEnum.InProgress)
        }
    }


    @PreAuthorize("hasAnyRole('Manager', 'Expert')")
    //SOLO MANAGER O EXPERT (SE EXPERT BISOGNA VERIFICARE CHE IL TICKET SIA SUO)
    override fun resolveTicket(ticketId: Int, resolverEmail: String) {
        //per vedere se è dell 'expert
        //ticketRepository.findTicketByIdAndExpertEmail(ticketId, resolverEmail) ?: throw TicketNotFoundException()
        val ticket: Ticket = ticketRepository.findByIdOrNull(ticketId) ?: throw TicketNotFoundException()

        val resolver = staffRepository.findByEmailIgnoreCase(resolverEmail) ?: throw ProfileNotFoundException()
        if (ticket.status == TicketStatusEnum.Open || ticket.status == TicketStatusEnum.Reopened || ticket.status == TicketStatusEnum.InProgress){
            ticket.status = TicketStatusEnum.Resolved
            ticket.statusHistory.add(ResolvedTicketStatus().apply {
                this.ticket = ticket
                by = resolver
                timestamp = LocalDateTime.now()
            })
            ticketRepository.save(ticket)
        } else {
            throw InvalidTicketStatusTransitionException(ticket.status, TicketStatusEnum.Resolved)
        }
    }

    @PreAuthorize("hasAnyRole('Manager', 'Expert')")
    //SOLO MANAGER O EXPERT (SE EXPERT BISOGNA VERIFICARE CHE IL TICKET SIA SUO)
    override fun closeTicket(ticketId: Int, closerEmail: String) {
        val ticket: Ticket = ticketRepository.findByIdOrNull(ticketId) ?: throw TicketNotFoundException()
        val closer = staffRepository.findByEmailIgnoreCase(closerEmail) ?: throw ProfileNotFoundException()

        if (ticket.status != TicketStatusEnum.Closed) {
            ticket.status = TicketStatusEnum.Closed
            ticket.expert = null
            ticket.priorityLevel = null
            ticket.statusHistory.add(ClosedTicketStatus().apply {
                this.ticket = ticket
                by = closer
            })
            ticketRepository.save(ticket)
        } else {
            throw InvalidTicketStatusTransitionException(ticket.status, TicketStatusEnum.Closed)
        }
    }

    override fun getFinalStatus(ticketId: Int): TicketStatusDTO {
        val statuses = ticketStatusRepository.findByTicketAndTimestampIsMaximum(ticketId)
        return statuses.toDTO()
    }

}