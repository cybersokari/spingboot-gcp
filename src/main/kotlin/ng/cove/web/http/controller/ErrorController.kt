package ng.cove.web.http.controller

import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
class ErrorController: ErrorController {
    @RequestMapping("/error")
    fun handleError(): ResponseEntity<Any> {
        return ResponseEntity.notFound().build()
    }
}