package co.gatedaccess.web.http.interceptor

import co.gatedaccess.web.data.repo.MemberRepo
import co.gatedaccess.web.data.repo.SecurityGuardRepo
import com.google.api.core.ApiFuture
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseToken
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
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
        println("Admin interceptor is working")
        var idToken = request.getHeader("Authorization")

        if (idToken != null) {
            try {
                idToken =
                    idToken.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1] //Remove Bearer prefix
                println("Bearer is: $idToken")

                val userId : String
                val userType : String
                // Running on dev, skip token decode
                if (request.requestURL.toString().startsWith("http://localhost:8080")) {
                    userType = "admin" // Manually update this to any type when running on dev
                    userId = idToken
                } else {
                    val tokenAsync : ApiFuture<FirebaseToken> = FirebaseAuth.getInstance()
                        .verifyIdTokenAsync(idToken, true)
                    val firebaseToken = tokenAsync[5, TimeUnit.SECONDS]
                    userType = firebaseToken.claims["type"].toString()
                    userId = firebaseToken.uid
                }

                // Non admins cannot access admin routes
                if (userType != "admin" && request.requestURI.contains("/admin")){
                    println("User type : $userType cannot access admin route")
                    response.sendError(401, "Unauthorized user")
                    return false
                }


                /** Check if device-id in request header matches the one in db**/
                val savedDeviceId: String
                if(arrayOf("admin" ,"member").contains(userType)){
                    val memberRepo = context.getBean(MemberRepo::class.java)
                    val member = memberRepo.findById(userId).orElseThrow()!!
                    request.setAttribute("user", member)
                    savedDeviceId = member.deviceId!!
                }else{
                    val guardRepo = context.getBean(SecurityGuardRepo::class.java)
                    val guard = guardRepo.findById(userId).orElseThrow()!!
                    request.setAttribute("user", guard)
                    savedDeviceId = guard.deviceId!!
                }
                request.getHeader("x-device-id").let {
                    if (it != savedDeviceId){
                        response.sendError(409, "Unauthorized device")
                        return false
                    }
                }
                /** End Device ID check**/

                return true
            } catch (e: Exception) {
                LoggerFactory.getLogger(this::class.java.packageName).info(e.localizedMessage)
            }
        }
        response.sendError(401, "User is not logged in")
        return false
    }

}
