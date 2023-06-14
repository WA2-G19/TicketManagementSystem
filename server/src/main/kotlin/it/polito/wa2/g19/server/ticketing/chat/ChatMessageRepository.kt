package it.polito.wa2.g19.server.ticketing.chat

import kotlinx.coroutines.flow.Flow
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatMessageRepository: CoroutineCrudRepository<ChatMessage, Int> {


    fun findByTicketId(ticketId: Int): Flow<ChatMessage>
}


@Repository
interface ChatMessageRepositoryJPA: JpaRepository<ChatMessage, Int> {


    fun findByTicketId(ticketId: Int): Flow<ChatMessage>
}