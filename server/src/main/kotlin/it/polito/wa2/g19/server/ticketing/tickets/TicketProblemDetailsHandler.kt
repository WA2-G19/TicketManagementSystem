package it.polito.wa2.g19.server.ticketing.tickets

import it.polito.wa2.g19.server.products.ProductNotFoundException
import it.polito.wa2.g19.server.ticketing.statuses.InvalidTicketStatusTransitionException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class TicketProblemDetailsHandler: ResponseEntityExceptionHandler() {

    @ExceptionHandler(InvalidTicketStatusTransitionException::class)
    fun handleInvalidTicketStatusTransition(e: InvalidTicketStatusTransitionException) = ProblemDetail
        .forStatusAndDetail( HttpStatus.BAD_REQUEST, e.message!! )


}