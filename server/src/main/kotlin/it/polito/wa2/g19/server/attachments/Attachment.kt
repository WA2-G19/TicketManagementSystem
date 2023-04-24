package it.polito.wa2.g19.server.attachments

import it.polito.wa2.g19.server.chat.ChatMessage
import it.polito.wa2.g19.server.common.EntityBase
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "attachment")
class Attachment(): EntityBase<Int>() {

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", nullable = false)
    lateinit var message: ChatMessage

    @Column(nullable = false)
    var contentType: String = ""
    @Column(nullable = false)
    var length: Int = 0
    @Column(nullable = false)
    var content: ByteArray = ByteArray(length)
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    var timestamp: LocalDateTime = LocalDateTime.now()

}