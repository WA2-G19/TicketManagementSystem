package it.polito.wa2.g19.server.ticketing.attachments

import it.polito.wa2.g19.server.ticketing.chat.ChatMessage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AttachmentRepository: JpaRepository<Attachment, Int> {

    fun findByMessage(chatMessage: ChatMessage): List<AttachmentProjection>

}