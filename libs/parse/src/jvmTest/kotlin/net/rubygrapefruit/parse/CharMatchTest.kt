package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.text.literal
import net.rubygrapefruit.parse.text.match
import kotlin.test.Test

class CharMatchTest : AbstractParseTest() {
    @Test
    fun `matches char literal and produces matching input`() {
        val parser = match(literal("abc"))

        parser.expecting {
            expectMatch {
                expectLiteral("abc")
            }
        }

        parser.matches("abc", expected = "abc")

        parser.doesNotMatch("") {
            expectLiteral("abc")
        }
    }
}