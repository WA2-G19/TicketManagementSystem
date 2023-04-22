package it.polito.wa2.g19.server.ticketing

interface ChatMessageService {

    fun getChatMessage(id: Int): ChatMessageDTO

    fun getChatMessages(): Set<ChatMessageDTO>

    fun insertChatMessage(message: ChatMessageDTO) // Missing how to pass the attachments
}