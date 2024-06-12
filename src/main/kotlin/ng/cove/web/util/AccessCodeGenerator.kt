package ng.cove.web.util

import java.security.SecureRandom

class AccessCodeGenerator {

    fun getCode(length: Int = 1): String {
        val sb = StringBuilder()
        val random = SecureRandom()
        for (i in 0 until length) {
            sb.append(random.nextInt(10)) // Append a random digit (0-9)
        }
        return sb.toString()
    }
}
