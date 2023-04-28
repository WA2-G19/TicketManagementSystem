package it.polito.wa2.g19.server.chat

import org.springframework.web.multipart.MultipartFile

interface ChatMessageService {

    fun getChatMessage(id: Int): ChatMessageDTO

    fun getChatMessages(): Set<ChatMessageDTO>

    fun insertChatMessage(messageToSave: ChatMessageDTO, files: List<MultipartFile>)

}