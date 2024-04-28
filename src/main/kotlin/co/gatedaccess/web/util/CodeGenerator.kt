package co.gatedaccess.web.util

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import java.security.SecureRandom

@Component
class CodeGenerator {
    @Autowired
    lateinit var environment: Environment

    /**
     * Returns a numeric string with a specified length
     *
     * @return code
     */
    fun getCode(): String {
        val codeLength: Int = environment.getRequiredProperty("visitor.access-code.length", Int::class.java)
        val sb = StringBuilder()
        val random = SecureRandom()
        for (i in 0 until codeLength) {
            sb.append(random.nextInt(10)) // Append a random digit (0-9)
        }
        return sb.toString()
    }
}
