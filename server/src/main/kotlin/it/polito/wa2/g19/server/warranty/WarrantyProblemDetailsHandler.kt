package it.polito.wa2.g19.server.warranty

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class WarrantyProblemDetailsHandler: ResponseEntityExceptionHandler() {

    @ExceptionHandler(WarrantyNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleWarrantyNotFound(e: WarrantyNotFoundException) = ProblemDetail
        .forStatusAndDetail( HttpStatus.NOT_FOUND, e.message!! )

    @ExceptionHandler(WarrantyExpiredException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleWarrantyExpired(e: WarrantyExpiredException) = ProblemDetail
        .forStatusAndDetail( HttpStatus.FORBIDDEN, e.message!! )

    @ExceptionHandler(WarrantyAlreadyActivated::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleWarrantyAlreadyActivated(e: WarrantyAlreadyActivated) = ProblemDetail
        .forStatusAndDetail( HttpStatus.CONFLICT, e.message!! )
}