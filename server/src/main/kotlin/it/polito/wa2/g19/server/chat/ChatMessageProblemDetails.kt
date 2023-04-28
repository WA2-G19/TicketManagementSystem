package it.polito.wa2.g19.server.chat

import it.polito.wa2.g19.server.products.ProductNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class ChatMessageProblemDetails: ResponseEntityExceptionHandler() {

    @ExceptionHandler(MessageNotFoundException::class)
    fun handleProductNotFound(e: MessageNotFoundException) = ProblemDetail
        .forStatusAndDetail( HttpStatus.NOT_FOUND, e.message!! )

}