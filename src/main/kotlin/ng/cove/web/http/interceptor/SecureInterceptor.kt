package ng.cove.web.http.interceptor

import com.google.api.core.ApiFuture
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseToken
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import ng.cove.web.data.model.UserType
import ng.cove.web.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.core.env.StandardEnvironment
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.servlet.HandlerInterceptor
import java.util.concurrent.TimeUnit
import javax.annotation.Nonnull

@Component
class SecureInterceptor(val context: WebApplicationContext) : HandlerInterceptor {

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

                val userId: String
                val userType: UserType
                // Running on dev, skip token decode
                if (StandardEnvironment().activeProfiles[0] == "dev") {
                    userType = UserType.Member // Manually update this to any type when running on dev
                    userId = idToken
                    println("Bearer is: $idToken")
                } else {
                    val tokenAsync: ApiFuture<FirebaseToken> = FirebaseAuth.getInstance()
                        .verifyIdTokenAsync(idToken, true)
                    val firebaseToken = tokenAsync[10, TimeUnit.SECONDS]
                    userType = UserType.valueOf(firebaseToken.claims["type"] as String)
                    userId = firebaseToken.uid
                }

                val userService = context.getBean(UserService::class.java)
                /** Get User model from DB and attach to request**/
                if (userType == UserType.Guard) {
                    val guard = userService.getGuardById(userId)!!
                    request.setAttribute("user", guard)
                } else {
                    val member = userService.getMemberById(userId)!!
                    request.setAttribute("user", member)
                }
                return true
            } catch (e: Exception) {
                LoggerFactory.getLogger(this::class.java.simpleName).warn(e.localizedMessage)
            }
        }

        response.status = HttpStatus.UNAUTHORIZED.value()
        return false
    }

}
