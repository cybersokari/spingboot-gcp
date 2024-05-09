package ng.cove.web.util

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.SecureRandom

@Component
class CodeGenerator {

    @Value("\${visitor.access-code.length}")
    val accessCodeLength: Int? = null

    fun getCode(): String {
        val sb = StringBuilder()
        val random = SecureRandom()
        for (i in 0 until accessCodeLength!!) {
            sb.append(random.nextInt(10)) // Append a random digit (0-9)
        }
        return sb.toString()
    }
}
