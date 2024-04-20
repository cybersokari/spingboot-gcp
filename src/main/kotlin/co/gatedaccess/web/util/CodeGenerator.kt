package co.gatedaccess.web.util

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.util.*

@Component
class CodeGenerator {
    @Autowired
    var environment: Environment? = null

    /**
     * Returns a numeric string with a specified length
     *
     * @return code
     */
    fun getCode(type: CodeType?): String {
        val CODE_LENGTH: Int
        when (type) {
            CodeType.guard -> {
                CODE_LENGTH = environment!!.getRequiredProperty("security-guard.otp.length", Int::class.java)
                return UUID.randomUUID().toString().replace("-", "")
                    .substring(0, CODE_LENGTH) // Adjust length as needed
            }

            CodeType.visitor -> {
                CODE_LENGTH = environment!!.getRequiredProperty("visitor.access-code.length", Int::class.java)
                val sb = StringBuilder()
                val random = SecureRandom()
                for (i in 0 until CODE_LENGTH) {
                    sb.append(random.nextInt(10)) // Append a random digit (0-9)
                }
                return sb.toString()
            }

            CodeType.community -> {
                CODE_LENGTH = environment!!.getRequiredProperty("community.invite-code.length", Int::class.java)
                return UUID.randomUUID().toString().replace("-", "")
                    .substring(0, CODE_LENGTH) // Adjust length as needed
            }

            else -> {
                return ""
            }
        }
    }
}
