package it.polito.wa2.g19.server.ticketing.tickets

import it.polito.wa2.g19.server.products.ProductNotFoundException
import it.polito.wa2.g19.server.products.ProductRepository
import it.polito.wa2.g19.server.profiles.customers.CustomerRepository
import it.polito.wa2.g19.server.profiles.ProfileNotFoundException
import it.polito.wa2.g19.server.profiles.staff.Expert
import it.polito.wa2.g19.server.profiles.staff.Manager
import it.polito.wa2.g19.server.profiles.staff.StaffRepository
import it.polito.wa2.g19.server.ticketing.statuses.*
import org.springframework.data.repository.findByIdOrNull
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
    override fun getTicket(id: Int): TicketOutDTO {
        if (ticketRepository.existsById(id)) {
            return ticketRepository.findByIdOrNull(id)!!.toOutDTO()
        }
        throw TicketNotFoundException()
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

    override fun reopenTicket(ticketId: Int) {
        val ticket: Ticket = ticketRepository.findByIdOrNull(ticketId) ?: throw TicketNotFoundException()


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

    override fun resolveTicket(ticketId: Int, resolverEmail: String) {
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

}