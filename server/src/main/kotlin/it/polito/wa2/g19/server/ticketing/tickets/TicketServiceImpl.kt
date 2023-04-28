package it.polito.wa2.g19.server.ticketing.tickets

import it.polito.wa2.g19.server.products.ProductNotFoundException
import it.polito.wa2.g19.server.products.ProductRepository
import it.polito.wa2.g19.server.profiles.CustomerRepository
import it.polito.wa2.g19.server.profiles.ProfileNotFoundException
import it.polito.wa2.g19.server.ticketing.statuses.OpenTicketStatus
import it.polito.wa2.g19.server.ticketing.statuses.TicketStatusEnum
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TicketServiceImpl(
    private val ticketRepository: TicketRepository,
    private val customerRepository: CustomerRepository,
    private val productRepository: ProductRepository
): TicketService {
    override fun getTicket(id: Int): TicketOutDTO {
        if (ticketRepository.existsById(id)) {
            return ticketRepository.findByIdOrNull(id)!!.toOutDTO()
        }
        throw TicketNotFoundException()
    }

    override fun getTickets(): Set<TicketOutDTO> {
        return ticketRepository.findAll().map{ it.toOutDTO() }.toSet()
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
}