package it.polito.wa2.g19.server.repositories.reactive

import it.polito.wa2.g19.server.ticketing.chat.ChatMessage
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatMessageRepository: CoroutineCrudRepository<ChatMessage, Int> {

    @Query("select * from chat_message CM where CM.ticket_id = :ticketId")
    suspend fun findByTicketId(ticketId: Int): Flow<ChatMessage>
}