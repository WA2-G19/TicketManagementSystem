package it.polito.wa2.g19.server.ticketing.tickets

import it.polito.wa2.g19.server.profiles.customers.Customer
import it.polito.wa2.g19.server.profiles.staff.Staff
import it.polito.wa2.g19.server.ticketing.statuses.PriorityLevel
import it.polito.wa2.g19.server.ticketing.statuses.TicketStatusEnum
import it.polito.wa2.g19.server.warranty.Warranty


import org.springframework.data.jpa.domain.Specification


class TicketSpecification{

    companion object{
        fun ofExpert(expert: Staff?): Specification<Ticket>{
                return Specification<Ticket> { root, _, builder ->
                    if(expert != null)
                        builder.equal(root.get<Staff>("expert"), expert)
                    else {
                        null
                    }
                }
        }

        fun ofCustomer(customer: Customer?): Specification<Ticket>{
            return Specification<Ticket> { root, _, builder ->
                if(customer != null)
                    builder.equal(root.get<Warranty>("warranty").get<Customer>("customer"), customer)
                else {
                    null
                }
            }
        }

        fun ofPriority(priorityLevel: PriorityLevel?): Specification<Ticket>{
            return Specification<Ticket> { root, _, builder ->
                if(priorityLevel != null)
                    builder.equal(root.get<PriorityLevel>("priorityLevel"), priorityLevel)
                else {
                    null
                }
            }
        }

        fun ofStatus(status: TicketStatusEnum?): Specification<Ticket>{
            return Specification<Ticket> { root, _, builder ->
                if(status != null)
                    builder.equal(root.get<TicketStatusEnum>("status"), status)
                else {
                    null
                }
            }
        }
    }
}



