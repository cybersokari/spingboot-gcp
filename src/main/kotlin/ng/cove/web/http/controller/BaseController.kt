package ng.cove.web.http.controller

import com.mongodb.lang.NonNull
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingRequestHeaderException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.NoHandlerFoundException


@ControllerAdvice
abstract class BaseController {

    @ExceptionHandler(MissingRequestHeaderException::class)
    fun handleMissingHeader(@NonNull ex: MissingRequestHeaderException): ResponseEntity<String> {
        return ResponseEntity.badRequest().body("Required header '" + ex.headerName + "' is missing.")
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(@NonNull ex: MethodArgumentNotValidException): ResponseEntity<String> {
        return ResponseEntity.badRequest()
            .body(ex.bindingResult.fieldError?.defaultMessage ?: "Required argument is missing.")
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadable(@NonNull ex: HttpMessageNotReadableException): ResponseEntity<String> {
        return ResponseEntity.badRequest().body(ex.cause?.localizedMessage ?: ex.localizedMessage)
    }

    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNoHandlerFoundException(
        ex: NoHandlerFoundException?, httpServletRequest: HttpServletRequest?
    ): ResponseEntity<Any> {
        return ResponseEntity.notFound().build()
    }

}