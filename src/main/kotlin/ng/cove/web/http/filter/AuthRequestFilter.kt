package ng.cove.web.http.filter

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseToken
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import ng.cove.web.data.model.UserRole
import ng.cove.web.data.repo.AdminRepo
import ng.cove.web.data.repo.MemberRepo
import ng.cove.web.data.repo.SecurityGuardRepo
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.filter.OncePerRequestFilter

class AuthRequestFilter(val context: WebApplicationContext): OncePerRequestFilter() {
    public override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val bearer : String? = request.getHeader("Authorization")

        if (bearer != null){
            try {
                val idToken =
                    bearer.split(" ").dropLastWhile { it.isEmpty() }[1] //Remove Bearer prefix

                val firebaseToken: FirebaseToken = FirebaseAuth.getInstance()
                    .verifyIdToken(idToken, true)
                val userRole = UserRole.valueOf(firebaseToken.claims["role"] as String)
                val userId = firebaseToken.uid

                /** Get User model from DB and attach to request**/
                val user : Any
                when(userRole){
                    UserRole.MEMBER -> {
                        val repo = context.getBean(MemberRepo::class.java)
                        user = repo.findFirstById(userId)!!
                    }
                    UserRole.GUARD -> {
                        val repo = context.getBean(SecurityGuardRepo::class.java)
                        user = repo.findFirstById(userId)!!
                    }
                    UserRole.ADMIN -> {
                        val repo = context.getBean(AdminRepo::class.java)
                        user = repo.findFirstById(userId)!!
                    }
                }
                request.setAttribute("user", user)
                filterChain.doFilter(request, response)
                return
            } catch (e: Exception) {
                LoggerFactory.getLogger(this::class.java.simpleName).warn(e.localizedMessage)
            }
        }
        response.status = HttpStatus.UNAUTHORIZED.value()
    }
}