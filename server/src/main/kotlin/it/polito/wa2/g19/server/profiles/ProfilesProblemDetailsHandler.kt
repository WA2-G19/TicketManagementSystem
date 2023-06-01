package it.polito.wa2.g19.server.profiles

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class ProfilesProblemDetailsHandler: ResponseEntityExceptionHandler() {
    @ExceptionHandler(ProfileNotFoundException::class)
    fun handleProfileNotFound(e: ProfileNotFoundException) = ProblemDetail
        .forStatusAndDetail( HttpStatus.NOT_FOUND, e.message!! )

    @ExceptionHandler(DuplicateEmailException::class)
    fun handleDuplicateEmail(e: DuplicateEmailException) = ProblemDetail
        .forStatusAndDetail( HttpStatus.CONFLICT, e.message!! )

    @ExceptionHandler(NotMatchingEmailException::class)
    fun handleNotMatchingEmail(e: NotMatchingEmailException) = ProblemDetail
        .forStatusAndDetail( HttpStatus.BAD_REQUEST, e.message!! )

    @ExceptionHandler(ProfileAlreadyPresent::class)
    fun handleProfileAlreadyPresent(e: ProfileAlreadyPresent) = ProblemDetail
        .forStatusAndDetail( HttpStatus.CONFLICT, e.message!! )

    @ExceptionHandler(KeycloakException::class)
    fun handleProfileAlreadyPresent(e: KeycloakException): ProblemDetail {
        println("----------------------------------------------------")
        return ProblemDetail
            .forStatusAndDetail( HttpStatus.INTERNAL_SERVER_ERROR, e.message!! )}
}