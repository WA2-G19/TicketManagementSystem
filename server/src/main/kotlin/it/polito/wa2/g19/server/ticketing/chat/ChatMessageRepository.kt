package it.polito.wa2.g19.server.ticketing.chat

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatMessageRepository: JpaRepository<ChatMessage, Int> {
    fun findByTicketIdAndId(ticketId: Int, chatMessageId: Int): ChatMessage?

    fun findByTicketId(ticketId: Int): List<ChatMessage>?
}