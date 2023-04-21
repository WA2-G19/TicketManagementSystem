package it.polito.wa2.g19.server.ticketing

import it.polito.wa2.g19.server.profiles.Customer
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
class ChatMessage() {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Int? = null

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    lateinit var ticket: Ticket
    @ManyToOne
    @JoinColumn(name = "author_id")
    lateinit var author: Customer
    var body: String = ""
    @OneToMany(mappedBy = "message")
    lateinit var attachments: List<Attachment>

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    var timestamp: LocalDateTime = LocalDateTime.now()
}

@Entity
@Table(name = "attachment")
class Attachment() {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Int? = null

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    lateinit var message: ChatMessage

    var contentType: String = ""
    var length: Int = 0
    var content: ByteArray = ByteArray(length)
    @Temporal(TemporalType.TIMESTAMP)
    var timestamp: LocalDateTime = LocalDateTime.now()
}