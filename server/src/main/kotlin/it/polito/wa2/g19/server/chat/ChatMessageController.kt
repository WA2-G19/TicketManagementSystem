package it.polito.wa2.g19.server.chat

import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@Validated
@RequestMapping("/API")
class ChatMessageController(
    private val chatMessageService: ChatMessageService
) {

    @GetMapping("/messages/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun getMessage(@PathVariable id: Int): ChatMessageDTO {
        return chatMessageService.getChatMessage(id)
    }

    @GetMapping("/messages")
    @ResponseStatus(HttpStatus.OK)
    fun getMessages(): Set<ChatMessageDTO> {
        return chatMessageService.getChatMessages()
    }

    @PostMapping("/messages")
    @ResponseStatus(HttpStatus.CREATED)
    fun insertChatMessage(@RequestBody chatMessage: ChatMessageDTO, @RequestPart files: List<MultipartFile>) {
        chatMessageService.insertChatMessage(chatMessage, files)
    }

}