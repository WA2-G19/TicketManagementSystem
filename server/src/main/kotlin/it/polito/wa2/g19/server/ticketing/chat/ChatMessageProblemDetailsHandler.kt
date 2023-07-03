package it.polito.wa2.g19.server.ticketing.chat

import it.polito.wa2.g19.server.ticketing.tickets.TicketNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class ChatMessageProblemDetailsHandler: ResponseEntityExceptionHandler() {

    @ExceptionHandler(MessageNotFoundException::class)
    fun handleProductNotFound(e: MessageNotFoundException) = ProblemDetail
        .forStatusAndDetail( HttpStatus.NOT_FOUND, e.message!! )

    @ExceptionHandler(TicketNotFoundException::class)
    fun handleProductNotFound(e: TicketNotFoundException) = ProblemDetail
        .forStatusAndDetail( HttpStatus.NOT_FOUND, e.message!! )

    @ExceptionHandler(AttachmentNotFoundException::class)
    fun handleProductNotFound(e: AttachmentNotFoundException) = ProblemDetail
        .forStatusAndDetail( HttpStatus.NOT_FOUND, e.message!! )

    @ExceptionHandler(AuthorAndUserAreDifferentException::class)
    fun handleDifferentAuthor(e: AuthorAndUserAreDifferentException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message!!)

    @ExceptionHandler(ChatClosedException::class)
    fun handleChatClosed(e: ChatClosedException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.CONFLICT, e.message!!)

}