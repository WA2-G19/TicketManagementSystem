package it.polito.wa2.g19.server.ticketing.chat

import it.polito.wa2.g19.server.ticketing.attachments.AttachmentDTO
import org.springframework.core.io.ByteArrayResource
import org.springframework.web.multipart.MultipartFile

interface ChatMessageService {

    fun getChatMessage(id: Int): ChatMessageOutDTO

    fun getChatMessages(): Set<ChatMessageOutDTO>

    fun insertChatMessage(ticketId: Int, messageToSave: ChatMessageInDTO, files: List<MultipartFile>?): Int

    fun getAttachment(ticketId: Int, attachmentId: Int): AttachmentDTO
}