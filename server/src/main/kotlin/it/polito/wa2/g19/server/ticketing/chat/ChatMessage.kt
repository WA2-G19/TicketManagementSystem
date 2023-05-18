package it.polito.wa2.g19.server.ticketing.chat

import it.polito.wa2.g19.server.common.EntityBase
import it.polito.wa2.g19.server.profiles.Profile
import it.polito.wa2.g19.server.profiles.customers.Customer
import it.polito.wa2.g19.server.profiles.staff.Staff
import it.polito.wa2.g19.server.ticketing.attachments.Attachment
import it.polito.wa2.g19.server.ticketing.tickets.Ticket
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity
@Table(
    name = "chat_message",
    indexes = [
        Index(name = "IX_chat_message_timestamp", columnList = "timestamp DESC"),
        Index(name = "IX_chat_message_ticket_id", columnList = "ticket_id DESC"),
        Index(name = "IX_chat_message_customer_author_id", columnList = "customer_author_id DESC"),
        Index(name = "IX_chat_message_staff_author_id", columnList = "staff_author_id DESC")
    ]
)
abstract class ChatMessage : EntityBase<Int>() {

    companion object {
        fun withAuthor(author: Profile): ChatMessage =
            when (author) {
                is Staff -> {
                    StaffChatMessage().apply {
                        staffAuthor = author
                    }
                }

                is Customer -> {
                    CustomerChatMessage().apply {
                        customerAuthor = author
                    }
                }

                else -> {
                    throw IllegalArgumentException("author cannot be of type ${author::class}")
                }
            }
    }

    @ManyToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    open lateinit var ticket: Ticket
    @OneToMany(mappedBy = "message")
    open lateinit var attachments: Set<Attachment>
    @Column(nullable = false)
    var body: String = ""
    @CreationTimestamp
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    var timestamp: LocalDateTime = LocalDateTime.now()

    abstract fun getAuthor(): Profile
}

@Entity
class CustomerChatMessage : ChatMessage() {
    @ManyToOne
    @NotNull
    @JoinColumn(name = "customer_author_id", nullable = true)
    lateinit var customerAuthor: Customer

    override fun getAuthor(): Profile = customerAuthor
}

@Entity
class StaffChatMessage : ChatMessage() {
    @ManyToOne
    @NotNull
    @JoinColumn(name = "staff_author_id", nullable = true)
    lateinit var staffAuthor: Staff

    override fun getAuthor(): Profile = staffAuthor
}