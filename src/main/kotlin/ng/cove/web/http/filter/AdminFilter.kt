package ng.cove.web.http.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import ng.cove.web.data.model.Admin
import ng.cove.web.data.model.Member
import ng.cove.web.data.repo.CommunityRepo
import org.springframework.http.HttpStatus
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.filter.OncePerRequestFilter

/***
 * This interceptor runs after [AuthRequestFilter] is successful.
 * It checks if the user is an admin of their community.
 * There is an assumption that [AuthRequestFilter] sets an [Admin] user as a request attribute.
 */
class AdminFilter(val context: WebApplicationContext) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val admin = request.getAttribute("user") as Admin
        val community = context.getBean(CommunityRepo::class.java)
            .findCommunityByIdAndAdminsContains(admin.communityId!!, admin.id!!)

        if(community != null){
            filterChain.doFilter(request,response)
        }else{
            response.status = HttpStatus.UNAUTHORIZED.value()
        }
    }
}