package it.polito.wa2.g19.server.products

import org.json.JSONObject
import org.springframework.http.*
import org.springframework.validation.FieldError

import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class ProductsProblemDetailsHandler: ResponseEntityExceptionHandler() {

    @ExceptionHandler(ProductNotFoundException::class)
    fun handleProductNotFound(e: ProductNotFoundException) = ProblemDetail
        .forStatusAndDetail( HttpStatus.NOT_FOUND, e.message!! )

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        super.handleMethodArgumentNotValid(ex, headers, status, request)
        val fieldErrors = ex.bindingResult.fieldErrors

        return ResponseEntity( ErrorResponse(fieldErrors, status, request).toJSON(), headers,  HttpStatus.FORBIDDEN)
    }
}

class ErrorResponse(val fieldErrors: List<FieldError>, val status: HttpStatusCode, val request: WebRequest){
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



