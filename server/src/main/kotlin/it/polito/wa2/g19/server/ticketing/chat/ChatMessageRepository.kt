package it.polito.wa2.g19.server.ticketing.chat

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ChatMessageRepository: JpaRepository<ChatMessage, Int> {
    fun findByTicketIdAndId(ticketId: Int, chatMessageId: Int): Optional<ChatMessage>

    fun findByTicketId(ticketId: Int): Optional<List<ChatMessage>>
}