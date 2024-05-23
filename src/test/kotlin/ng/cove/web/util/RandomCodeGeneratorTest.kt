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
class RandomCodeGeneratorTest {

    @Value("\${visitor.access-code.length}")
    val accessCodeLength = 1

    @Test
    fun givenCodeIsGenerated_whenGetCodeIsCalledAgain_theReturnedCodeShouldBeDifferent() {
        val generator = RandomCodeGenerator()
        val firstCode = generator.getCode()
        val secondCode = generator.getCode()

        assertEquals(firstCode.length, secondCode.length, "Code is the same length")
        assertNotEquals(firstCode, secondCode, "Codes are different")
    }

    @Test
    fun givenCodeIsProvided_whenGetCodeIsCalled_theReturnedCodeShouldBeOfConfiguredLength() {
        val generator = RandomCodeGenerator(accessCodeLength)
        val firstCode = generator.getCode()
        val secondCode = generator.getCode()

        assertEquals(firstCode.length, accessCodeLength, "Code length is as configured")
        assertEquals(firstCode.length, secondCode.length, "Code is the same length")
    }
}