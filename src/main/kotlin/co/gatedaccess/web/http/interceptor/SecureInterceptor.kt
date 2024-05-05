package co.gatedaccess.web.http.interceptor

import co.gatedaccess.web.data.model.UserType
import co.gatedaccess.web.data.repo.MemberRepo
import co.gatedaccess.web.data.repo.SecurityGuardRepo
import com.google.api.core.ApiFuture
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseToken
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.core.env.StandardEnvironment
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
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


                val userId : String
                val userType : UserType
                // Running on dev, skip token decode
                if (StandardEnvironment().activeProfiles[0] == "dev") {
                    userType = UserType.Admin // Manually update this to any type when running on dev
                    userId = idToken
                    println("Bearer is: $idToken")
                } else {
                    val tokenAsync : ApiFuture<FirebaseToken> = FirebaseAuth.getInstance()
                        .verifyIdTokenAsync(idToken, true)
                    val firebaseToken = tokenAsync[10, TimeUnit.SECONDS]
                    userType = UserType.valueOf(firebaseToken.claims["type"] as String)
                    userId = firebaseToken.uid
                }

                /** Get User model from DB and attach to request**/
                if(userType == UserType.Guard){
                    val guardRepo = context.getBean(SecurityGuardRepo::class.java)
                    val guard = guardRepo.findById(userId).orElseThrow()!!
                    request.setAttribute("user", guard)
                }else{
                    val memberRepo = context.getBean(MemberRepo::class.java)
                    val member = memberRepo.findById(userId).orElseThrow()!!
                    request.setAttribute("user", member)
                }
                return true
            } catch (e: Exception) {
                LoggerFactory.getLogger(this::class.java.simpleName).info(e.localizedMessage)
            }
        }
        response.sendError(HttpStatus.UNAUTHORIZED.value(), "User is not logged in")
        return false
    }

}
