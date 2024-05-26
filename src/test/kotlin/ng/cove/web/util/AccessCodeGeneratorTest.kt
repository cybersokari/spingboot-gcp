package ng.cove.web.util

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Value
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@ExtendWith(SpringExtension::class)
@TestPropertySource("/application.properties")
class AccessCodeGeneratorTest {

    @Value("\${visitor.access-code.length}")
    val accessCodeLength = 1

    @Test
    fun givenCodeIsGenerated_whenGetCodeIsCalledAgain_theReturnedCodeShouldBeDifferent() {
        val generator = AccessCodeGenerator()
        val firstCode = generator.getCode(accessCodeLength)
        val secondCode = generator.getCode(accessCodeLength)

        assertEquals(firstCode.length, secondCode.length, "Code is the same length")
        assertNotEquals(firstCode, secondCode, "Codes are different")
    }

    @Test
    fun givenCodeIsProvided_whenGetCodeIsCalled_theReturnedCodeShouldBeOfConfiguredLength() {
        val generator = AccessCodeGenerator()
        val firstCode = generator.getCode(accessCodeLength)
        val secondCode = generator.getCode(accessCodeLength)

        assertEquals(firstCode.length, accessCodeLength, "1st Code length is as configured")
        assertEquals(secondCode.length, accessCodeLength, "2nd Code length is as configured")
    }
}