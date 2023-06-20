package it.polito.wa2.g19.server.ticketing.statuses

import it.polito.wa2.g19.server.profiles.ProfileNotFoundException
import it.polito.wa2.g19.server.profiles.staff.StaffRepository
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration

@Service
@Transactional("transactionManager")

class TicketStatusServiceImpl(
    private val ticketStatusRepository: TicketStatusRepository,
    private val staffRepository: StaffRepository
): TicketStatusService {

    @PreAuthorize("hasRole('Manager')")
    override fun getTicketClosedByExpert(expertMail: String): Int {
        val expert = staffRepository.findByEmailIgnoreCase(expertMail) ?: throw ProfileNotFoundException()
        return ticketStatusRepository.getTicketsClosedByExpert(
            expert.email,
        )
    }

    @PreAuthorize("hasRole('Manager')")
    override fun getAverageTimedByExpert(expertMail: String): Float {
        val expert = staffRepository.findByEmailIgnoreCase(expertMail) ?: throw ProfileNotFoundException()

        val ticketStatusList = ticketStatusRepository.getTicketStatusByExpert(
            expert.email
        )
        println("----------------")
        println(ticketStatusList.size)
        println("----------------")

        var diff = 0f
        var count = 0
        ticketStatusList.groupBy { it.ticket.getId()!! }.values.forEach {
            println("........")
            val normalizedSize = if(it.size%2 ==0) it.size else it.size - 1
            for(i in 0 until normalizedSize step 2) {
                diff += Duration.between(
                    it[i].timestamp,
                    it[i+1].timestamp
                ).seconds
                count ++
            }
        }
        println("----------------")
        println(count)
        println("----------------")
        return if(count == 0) 0f else diff/count
    }
}