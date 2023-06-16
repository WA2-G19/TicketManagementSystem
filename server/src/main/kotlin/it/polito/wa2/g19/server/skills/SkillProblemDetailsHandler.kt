package it.polito.wa2.g19.server.skills

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class SkillProblemDetailsHandler {

    @ExceptionHandler(SkillNotFoundException::class)
    fun handleSkillNotFoundException(e: SkillNotFoundException): ProblemDetail = ProblemDetail
        .forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)
}