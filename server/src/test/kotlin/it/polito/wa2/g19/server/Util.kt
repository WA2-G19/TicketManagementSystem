package it.polito.wa2.g19.server

import it.polito.wa2.g19.server.products.Product
import it.polito.wa2.g19.server.profiles.*
import it.polito.wa2.g19.server.ticketing.statuses.*
import it.polito.wa2.g19.server.ticketing.tickets.Ticket
import it.polito.wa2.g19.server.ticketing.tickets.TicketDTO
import java.sql.Timestamp

import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

class Util {

    companion object{

        val testTimestamp = Timestamp(1000)



        fun mockCustomers(): List<Customer>{
            return mutableListOf<Customer>().let {
                for (i in 0..3){
                    val c = Customer("customer${i}@test.test", "customer${i}Name", "customer${i}Surname", "customer${i}Address")
                    it.add(c)
                }
                it
            }
        }

        fun mockExperts(): List<Expert>{
            return mutableListOf<Expert>().let {
                for (i in 0..3){
                    val e = Expert("expert${i}@test.test", "expert${i}Name", "expert${i}Surname" )
                    it.add(e)
                }
                it
            }
        }

        fun mockManagers(): List<Manager>{
            return mutableListOf<Manager>().let {
                for (i in 0..3){
                    val m = Manager("manager${i}@test.test", "expert${i}Name", "expert${i}Surname" )
                    it.add(m)
                }
                it
            }
        }

        fun mockProduct(): Product{
            val p1 = Product("4935531465706","JMT X-ring 530x2 Gold 104 Open Chain With Rivet Link for Kawasaki KH 400 a 1976", "JMT")
            return p1
        }

        fun mockTicketDTO(): TicketDTO{
            val customer = mockCustomers()[0]
            val product = mockProduct()
            return TicketDTO(null, customer.email, product.ean, "testDescription")
        }



        fun mockPriorityLevels(): List<PriorityLevel>{
            return listOf<PriorityLevel>(
                PriorityLevel().let { it.name = "LOW"; it },
                PriorityLevel().let { it.name = "MEDIUM"; it },
                PriorityLevel().let { it.name = "HIGH"; it },
                PriorityLevel().let { it.name = "CRITICAL"; it },
            )
        }

        fun mockTicket(): Ticket{


            return  Ticket().let {

                it.description = "testDescription"
                it.status = TicketStatusEnum.Open
                it }
        }

        fun mockOpenTicketStatus(): OpenTicketStatus{
            return OpenTicketStatus().let {
                it.ticket = mockTicket()

                it
            }
        }

    }

}

fun TicketDTO.equalsTo(other: TicketDTO): Boolean{
    return this.id == other.id && this.description == other.description
            && this.productEan == other.productEan
            && this.customerEmail == other.customerEmail
}