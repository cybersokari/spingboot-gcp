package co.gatedaccess.web.http.interceptor

import com.google.firebase.auth.FirebaseAuth
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import java.util.concurrent.TimeUnit
import java.util.logging.Logger
import javax.annotation.Nonnull

@Component
class SecureInterceptor : HandlerInterceptor {
    @Throws(Exception::class)
    override fun preHandle(
        request: HttpServletRequest,
        @Nonnull response: HttpServletResponse,
        @Nonnull handler: Any
    ): Boolean {
        var idToken = request.getHeader("Authorization")

        if (idToken != null) {
            try {
                idToken =
                    idToken.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1] //Remove Bearer prefix

                // Running on dev, skip token decode
                if (request.requestURL.toString().startsWith("http://localhost:8080")) {
                    println("Bearer is: $idToken")
                    request.setAttribute("user", idToken)
                    return true
                }

                val tokenAsync = FirebaseAuth.getInstance().verifyIdTokenAsync(idToken, true)
                val firebaseToken = tokenAsync[5, TimeUnit.SECONDS]
                request.setAttribute("user", firebaseToken.uid)
                return true
            } catch (e: Exception) {
                log.info(e.localizedMessage)
            }
        }

        response.contentType = "application/json"
        response.sendError(401, "Unauthorized user")
        return false
    }

    companion object {
        private val log: Logger = Logger.getLogger(SecureInterceptor::class.java.simpleName)
    }
}
