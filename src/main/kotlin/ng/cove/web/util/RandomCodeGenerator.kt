package ng.cove.web.util

import java.security.SecureRandom

class RandomCodeGenerator(private val accessCodeLength: Int = 1) {

    fun getCode(): String {
        val sb = StringBuilder()
        val random = SecureRandom()
        for (i in 0 until accessCodeLength) {
            sb.append(random.nextInt(10)) // Append a random digit (0-9)
        }
        return sb.toString()
    }
}
