package ng.cove.web.http.interceptor

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import ng.cove.web.data.model.Admin
import ng.cove.web.data.model.Member
import ng.cove.web.data.repo.CommunityRepo
import org.springframework.http.HttpStatus
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.servlet.HandlerInterceptor

/***
 * This interceptor runs after [SecureInterceptor] is successful.
 * It checks if the user is an admin of their community.
 * There is an assumption that [SecureInterceptor] sets a [Member] user as a request attribute.
 */
class AdminInterceptor(val context: WebApplicationContext) : HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val admin = request.getAttribute("user") as Admin
        val community = context.getBean(CommunityRepo::class.java)
            .findCommunityByIdAndAdminsContains(admin.communityId!!, admin.id!!)

        return if(community != null){
            true
        }else{
            response.status = HttpStatus.UNAUTHORIZED.value()
            false
        }
    }
}