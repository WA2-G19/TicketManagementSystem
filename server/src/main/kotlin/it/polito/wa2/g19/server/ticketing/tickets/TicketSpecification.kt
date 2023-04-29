package it.polito.wa2.g19.server.ticketing.tickets

import it.polito.wa2.g19.server.profiles.Customer
import it.polito.wa2.g19.server.profiles.Expert
import it.polito.wa2.g19.server.profiles.Staff
import it.polito.wa2.g19.server.ticketing.statuses.PriorityLevel
import it.polito.wa2.g19.server.ticketing.statuses.PriorityLevelEnum
import it.polito.wa2.g19.server.ticketing.statuses.TicketStatusEnum


import org.springframework.data.jpa.domain.Specification


class TicketSpecification{

    companion object{
        fun ofExpert(expert: Staff?): Specification<Ticket>{
                return Specification<Ticket> { root, query, builder ->
                    if(expert != null)
                        builder.equal(root.get<Staff>("expert"), expert)
                    else {
                        null
                    }
                }
        }

        fun ofCustomer(customer: Customer?): Specification<Ticket>{
            return Specification<Ticket> { root, query, builder ->
                if(customer != null)
                    builder.equal(root.get<Customer>("customer"), customer)
                else {
                    null
                }
            }
        }

        fun ofPriority(priorityLevel: PriorityLevel?): Specification<Ticket>{
            return Specification<Ticket> { root, query, builder ->
                if(priorityLevel != null)
                    builder.equal(root.get<PriorityLevel>("priorityLevel"), priorityLevel)
                else {
                    null
                }
            }
        }

        fun ofStatus(status: TicketStatusEnum?): Specification<Ticket>{
            return Specification<Ticket> { root, query, builder ->
                if(status != null)
                    builder.equal(root.get<TicketStatusEnum>("status"), status)
                else {
                    null
                }
            }
        }
    }
}



