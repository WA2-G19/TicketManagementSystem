package it.polito.wa2.g19.server.tickets

import it.polito.wa2.g19.server.products.ProductNotFoundException
import it.polito.wa2.g19.server.products.ProductRepository
import it.polito.wa2.g19.server.profiles.CustomerRepository
import it.polito.wa2.g19.server.profiles.ProfileNotFoundException
import it.polito.wa2.g19.server.tickets.statuses.OpenTicketStatus
import it.polito.wa2.g19.server.tickets.statuses.TicketStatus
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TicketServiceImpl(
    private val ticketRepository: TicketRepository,
    private val customerRepository: CustomerRepository,
    private val productRepository: ProductRepository
): TicketService {
    override fun getTicket(id: Int): TicketDTO {
        if (ticketRepository.existsById(id)) {
            return ticketRepository.findByIdOrNull(id)!!.toDTO()
        }
        throw TicketNotFoundException()
    }

    override fun getTickets(): Set<TicketDTO> {
        return ticketRepository.findAll().map{ it.toDTO() }.toSet()
    }

    override fun createTicket(ticket: TicketDTO) {
        val c = customerRepository.findByEmail(ticket.customerEmail) ?: throw ProfileNotFoundException()
        val p = productRepository.findByEan(ticket.productEan) ?: throw ProductNotFoundException()
        val t = Ticket().apply {
            customer = c
            product = p
            description = ticket.description
            statusHistory = mutableSetOf()
        }
        t.statusHistory.add(OpenTicketStatus().apply {
            this.ticket = t
            this.timestamp = LocalDateTime.now()
        })
        ticketRepository.save(t)
    }
}