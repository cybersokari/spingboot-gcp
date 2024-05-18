package ng.cove.web.http.interceptor

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseToken
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import ng.cove.web.data.model.UserType
import ng.cove.web.service.CacheService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.servlet.HandlerInterceptor
import javax.annotation.Nonnull

class SecureInterceptor(private val context: WebApplicationContext) : HandlerInterceptor {

    override fun preHandle(
        request: HttpServletRequest,
        @Nonnull response: HttpServletResponse,
        @Nonnull handler: Any
    ): Boolean {

        request.getHeader("Authorization")?.let { it ->
            try {
                val idToken =
                    it.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1] //Remove Bearer prefix

                val userId: String
                val userType: UserType
                // Running on dev, skip token decode
                if (context.environment.activeProfiles[0] == "dev") {
                    userType = UserType.Member // Manually update this to any type when running on dev
                    userId = idToken
                    println("Bearer is: $idToken")
                } else {
                    val firebaseToken: FirebaseToken = FirebaseAuth.getInstance()
                        .verifyIdToken(idToken, true)
                    userType = UserType.valueOf(firebaseToken.claims["type"] as String)
                    userId = firebaseToken.uid
                }

                val cacheService = context.getBean(CacheService::class.java)
                /** Get User model from DB and attach to request**/
                if (userType == UserType.Guard) {
                    val guard = cacheService.getGuardById(userId)!!
                    request.setAttribute("user", guard)
                } else {
                    val member = cacheService.getMemberById(userId)!!
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
