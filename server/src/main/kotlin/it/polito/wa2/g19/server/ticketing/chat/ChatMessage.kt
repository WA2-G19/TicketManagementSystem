package it.polito.wa2.g19.server.ticketing.chat

import it.polito.wa2.g19.server.common.EntityBase
import it.polito.wa2.g19.server.profiles.Profile
import it.polito.wa2.g19.server.profiles.customers.Customer
import it.polito.wa2.g19.server.profiles.staff.Staff
import it.polito.wa2.g19.server.ticketing.attachments.Attachment
import it.polito.wa2.g19.server.ticketing.tickets.ForbiddenException
import it.polito.wa2.g19.server.ticketing.tickets.Ticket
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.CreationTimestamp
import org.springframework.web.bind.annotation.RequestBody
import java.time.LocalDateTime
import java.util.UUID

@Entity

@org.springframework.data.relational.core.mapping.Table(
    name = "chat_message",
)
data class ChatMessage(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @org.springframework.data.annotation.Id

    var id: Int? = null,

    @Column(name = "ticket_id")
    open  var ticketId: Int,

    @Column(nullable = false)
    open var body: String = "",

    @CreationTimestamp
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    open var timestamp: LocalDateTime = LocalDateTime.now(),
    @Column(name = "customer_author_id")
    var customerAuthorId: UUID? = null,


    @Column(name = "staff_author_id")
    var staffAuthorId: UUID? = null
) {





    fun getAuthor(): UUID {

        if (customerAuthorId != null) {
            return this.customerAuthorId!!
        } else if (staffAuthorId != null) {
            return this.staffAuthorId!!
        }
        throw Exception("BOOM")
    }

    companion object {
        fun withAuthor(profile: Profile, ticket: Ticket, body: String): ChatMessage{
            val chatMessage = ChatMessage(null, ticket.getId()!!, body, LocalDateTime.now(), null, null)
            if (profile is Staff){
                chatMessage.staffAuthorId = profile.id
            } else if (profile is Customer) {
                chatMessage.customerAuthorId = profile.id
            } else{
                throw ForbiddenException()
            }
            return chatMessage

        }
    }
}




