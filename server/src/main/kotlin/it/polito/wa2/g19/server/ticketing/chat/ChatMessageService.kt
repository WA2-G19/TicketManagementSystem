package it.polito.wa2.g19.server.ticketing.chat

import it.polito.wa2.g19.server.ticketing.attachments.AttachmentDTO
import kotlinx.coroutines.flow.Flow
import org.springframework.web.multipart.MultipartFile

interface ChatMessageService {

    suspend fun getChatMessage(ticketId: Int,chatMessageId: Int): ChatMessageOutDTO

    fun getChatMessages(ticketId: Int): Flow<ChatMessageOutDTO>

    fun insertChatMessage(ticketId: Int, messageToSave: ChatMessageInDTO, files: List<MultipartFile>?): Int

    fun getAttachment(ticketId: Int, attachmentId: Int): AttachmentDTO
}