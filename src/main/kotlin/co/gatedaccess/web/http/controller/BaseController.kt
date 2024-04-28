package co.gatedaccess.web.http.controller

import com.mongodb.lang.NonNull
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingRequestHeaderException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestController

@RestController
abstract class BaseController {

    // Exception handler for MissingRequestHeaderException
    @ExceptionHandler(MissingRequestHeaderException::class)
    fun handleMissingHeader(@NonNull ex: MissingRequestHeaderException): ResponseEntity<String> {
        return ResponseEntity.badRequest().body("Required header '" + ex.headerName + "' is missing.")
    }

    // Exception handler for MethodArgumentNotValidException
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(@NonNull ex: MethodArgumentNotValidException): ResponseEntity<String> {
        return ResponseEntity.badRequest().body("Required argument '" + ex.parameter.parameterName + "' is missing.")
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadable(@NonNull ex: HttpMessageNotReadableException): ResponseEntity<String> {
        return ResponseEntity.badRequest().body(ex.cause?.localizedMessage?:ex.localizedMessage)
    }
}