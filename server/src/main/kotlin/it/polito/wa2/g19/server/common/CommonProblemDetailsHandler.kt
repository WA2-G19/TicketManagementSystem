package it.polito.wa2.g19.server.common

import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import it.polito.wa2.g19.server.ticketing.tickets.ForbiddenException
import jakarta.validation.ConstraintViolationException
import org.json.JSONObject
import org.springframework.http.*
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class CommonProblemDetailsHandler: ResponseEntityExceptionHandler() {

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val fieldErrors = ex.bindingResult.fieldErrors
        val h = HttpHeaders()
        headers.forEach { t, u -> h[t] = u }
        h.contentType = MediaType.APPLICATION_JSON
        return ResponseEntity( ErrorResponse(fieldErrors, status, request).toJSON(), h,  status)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(e: ConstraintViolationException): ProblemDetail{
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message!!)
    }

    @ExceptionHandler(ForbiddenException::class)
    fun handleForbiddenException(e: ForbiddenException): ProblemDetail{
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, e.message!!)
    }

    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val cause = ex.cause
        if (cause is MissingKotlinParameterException) {
            val h = HttpHeaders()
            headers.forEach { t, u -> h[t] = u }
            h.contentType = MediaType.APPLICATION_JSON
            val violations = setOf(createMissingKotlinParameterViolation(cause))
            return ResponseEntity(ErrorResponse(violations.toList(), status, request).toJSON(),h,status)
        }
        return super.handleHttpMessageNotReadable(ex, headers, status, request)
    }

    private fun createMissingKotlinParameterViolation(cause: MissingKotlinParameterException): FieldError {
        val name = cause.path.fold("") { jsonPath, ref ->
            val suffix = when {
                ref.index > -1 -> "[${ref.index}]"
                else -> ".${ref.fieldName}"
            }
            (jsonPath + suffix).removePrefix(".")
        }

        return FieldError("",name, "$name cannot be blank")
    }


}


class ErrorResponse(private val fieldErrors: List<FieldError>, val status: HttpStatusCode, val request: WebRequest){
    fun toJSON(): String{
        val response = JSONObject()
        response.put("type","about:blank")
        response.put("status", status.value())
        response.put("title", (status as HttpStatus).reasonPhrase)
        response.put("instance", (request as ServletWebRequest).request.requestURI)
        val map = HashMap<String, String>()
        fieldErrors.forEach{
            val field = it.field
            val errorMessage = it.defaultMessage
            map[field] = errorMessage?: ""
        }
        response.put("detail", JSONObject(map))

        return response.toString()
    }
}