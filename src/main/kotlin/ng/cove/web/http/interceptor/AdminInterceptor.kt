package ng.cove.web.http.interceptor

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import ng.cove.web.data.model.Member
import org.springframework.http.HttpStatus
import org.springframework.web.servlet.HandlerInterceptor

/***
 * This interceptor runs after [SecureInterceptor] is successful.
 * It checks if the user is an admin of their community.
 * There is an assumption that [SecureInterceptor] sets a [Member] user as a request attribute.
 */
class AdminInterceptor : HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val member = request.getAttribute("user") as Member
        member.community?.adminIds?.let {
            if (it.contains(member.id))
                return true
        }
        response.status = HttpStatus.UNAUTHORIZED.value()
        return false
    }
}