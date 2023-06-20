package it.polito.wa2.g19.server.ticketing.chat

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ChatMessageRepository: JpaRepository<ChatMessage, Int> {

    @Query(value = "select cm from ChatMessage cm where cm.id = ?2 and cm.ticket.id = ?1")
    fun findByTicketIdAndId(ticketId: Int, chatMessageId: Int): ChatMessage?
    @Query(value = "select cm from ChatMessage cm where cm.ticket.id = ?1")
    fun findByTicketId(ticketId: Int): List<ChatMessage>?
}