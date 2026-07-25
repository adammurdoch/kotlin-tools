package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.oneOf
import net.rubygrapefruit.parse.general.endOfInput
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class ChoiceOfEndOfInputTest : AbstractParseTest() {
    @Test
    fun `matches end of input`() {
        val parser = oneOf(
            endOfInput(),
            literal("abc")
        )

        parser.expecting {
            expectChoice {
                expectEndOfInput()
                expectLiteral("abc")
            }
        }

        parser.matches("")
        parser.matches("abc")

        // missing
        parser.doesNotMatch("ab") {
            expectLiteral("abc")
            expectEndOfInput()
        }

        // unexpected/extra
        parser.doesNotMatch("X") {
            expectLiteral("abc")
            expectEndOfInput()
        }
    }
}