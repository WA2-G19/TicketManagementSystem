package it.polito.wa2.g19.server

import it.polito.wa2.g19.server.products.Product
import it.polito.wa2.g19.server.profiles.customers.Customer
import it.polito.wa2.g19.server.profiles.staff.Expert
import it.polito.wa2.g19.server.profiles.staff.Manager
import it.polito.wa2.g19.server.profiles.vendors.Vendor
import it.polito.wa2.g19.server.ticketing.statuses.*
import it.polito.wa2.g19.server.ticketing.tickets.Ticket
import it.polito.wa2.g19.server.ticketing.tickets.TicketDTO
import it.polito.wa2.g19.server.warranty.Warranty
import java.sql.Timestamp
import java.time.Duration
import java.time.LocalDateTime
import java.util.*

class Util {

    companion object{


        lateinit var  warrantyUUID: UUID
        val testTimestamp = Timestamp(1000)



        fun mockCustomers(): List<Customer>{
            return mutableListOf<Customer>().let {
                for (i in 0..3){
                    val c = Customer("customer${i}@test.test", "customer${i}Name", "customer${i}Surname", "customer${i}Address")
                    c.id = UUID.randomUUID()
                    it.add(c)
                }
                it
            }
        }

        fun mockMainCustomer(): Customer{
            val c = Customer("client@test.it", "customerName", "customerSurname", "customerAddress")
            c.id = UUID.randomUUID()
            return c
        }

        fun mockExperts(): List<Expert>{
            return mutableListOf<Expert>().let {
                for (i in 0..3){
                    val e = Expert("expert${i}@test.test", "expert${i}Name", "expert${i}Surname" )
                    e.id = UUID.randomUUID()
                    it.add(e)
                }
                it
            }
        }

        fun mockMainExpert(): Expert{
            val e = Expert("expert@test.it", "expertName", "expertSurname")
            e.id = UUID.randomUUID()
            return e
        }

        fun mockManagers(): List<Manager>{
            return mutableListOf<Manager>().let {
                for (i in 0..3){
                    val m = Manager("manager${i}@test.test", "expert${i}Name", "expert${i}Surname")
                    m.id = UUID.randomUUID()
                    it.add(m)
                }
                it
            }
        }

        fun mockMainManager(): Manager{
            val m = Manager("manager@test.it", "managerName", "managerSurname")
            m.id = UUID.randomUUID()
            return m
        }

        fun mockProduct(): Product {
            return Product(
                "4935531465706",
                "JMT X-ring 530x2 Gold 104 Open Chain With Rivet Link for Kawasaki KH 400 a 1976",
                "JMT"
            )
        }

        fun mockTicketDTO(): TicketDTO{
            return TicketDTO(null, warrantyUUID, "testDescription")
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

        fun mockInProgressTicketStatus(): InProgressTicketStatus{
            return InProgressTicketStatus().let {
                it.ticket = mockTicket()

                it
            }
        }

        fun mockVendor(): Vendor{
            return Vendor().let {
                it.id = UUID.randomUUID()
                it.email = "vendor@test.it"
                it.address = "testAddress"
                it.businessName = "testBusinessName"
                it.phoneNumber = "3330004444"

                it
            }
        }

        fun mockWarranty(product: Product, vendor: Vendor, customer: Customer): Warranty{
            return Warranty().let {
                it.customer = customer
                it.vendor = vendor
                it.product = product
                it.creationTimestamp = LocalDateTime.now().minusDays(3)
                it.activationTimestamp = LocalDateTime.now()
                it.duration = Duration.ofDays(4)
                it
            }
        }

        fun mockExpiredWarranty(product: Product, vendor: Vendor, customer: Customer): Warranty{
            return Warranty().let {
                it.customer = customer
                it.vendor = vendor
                it.product = product
                it.creationTimestamp = LocalDateTime.now().minusDays(3)
                it.activationTimestamp = LocalDateTime.now()
                it.duration = Duration.ofDays(1)
                it
            }
        }

        fun mockNotActivatedWarranty(product: Product, vendor: Vendor, customer: Customer): Warranty{
            return Warranty().let {
                it.customer = customer
                it.vendor = vendor
                it.product = product
                it.creationTimestamp = LocalDateTime.now().minusDays(3)
                it.duration = Duration.ofDays(4)
                it
            }
        }



    }

}

fun TicketDTO.equalsTo(other: TicketDTO): Boolean{
    return this.id == other.id && this.description == other.description
            && this.warrantyUUID == other.warrantyUUID
}