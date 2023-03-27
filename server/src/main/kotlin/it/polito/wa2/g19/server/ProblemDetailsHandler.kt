package it.polito.wa2.g19.server

import it.polito.wa2.g19.server.products.*
import it.polito.wa2.g19.server.profiles.*
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class ProblemDetailsHandler: ResponseEntityExceptionHandler() {

    @ExceptionHandler(ProductNotFoundException::class)
    fun handleProductNotFound(e: ProductNotFoundException) = ProblemDetail
        .forStatusAndDetail( HttpStatus.NOT_FOUND, e.message!! )

    @ExceptionHandler(ProfileNotFoundException::class)
    fun handleProfileNotFound(e: ProfileNotFoundException) = ProblemDetail
        .forStatusAndDetail( HttpStatus.NOT_FOUND, e.message!! )

    @ExceptionHandler(DuplicateEmailException::class)
    fun handleDuplicateEmail(e: DuplicateEmailException) = ProblemDetail
        .forStatusAndDetail( HttpStatus.CONFLICT, e.message!! )


}