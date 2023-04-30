package it.polito.wa2.g19.server.ticketing.chat

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

}