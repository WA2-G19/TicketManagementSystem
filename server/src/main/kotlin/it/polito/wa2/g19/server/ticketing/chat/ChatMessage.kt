package it.polito.wa2.g19.server.ticketing.chat

import it.polito.wa2.g19.server.common.EntityBase
import it.polito.wa2.g19.server.profiles.customers.Customer
import it.polito.wa2.g19.server.ticketing.attachments.Attachment
import it.polito.wa2.g19.server.ticketing.tickets.Ticket
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity
@Table(
    name = "chat_message",
    indexes = [
        Index(name = "IX_chat_message_timestamp", columnList = "timestamp DESC"),
        Index(name = "IX_chat_message_ticket_id", columnList = "ticket_id DESC"),
        Index(name = "IX_chat_message_author_id", columnList = "author_id DESC")
    ]
)
class ChatMessage(): EntityBase<Int>() {

    @ManyToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    lateinit var ticket: Ticket
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    lateinit var author: Customer
    @OneToMany(mappedBy = "message")
    lateinit var attachments: Set<Attachment>
    @Column(nullable = false)
    var body: String = ""
    @CreationTimestamp
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    var timestamp: LocalDateTime = LocalDateTime.now()

}