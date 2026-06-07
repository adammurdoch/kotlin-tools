package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.literal
import net.rubygrapefruit.parse.text.literal
import net.rubygrapefruit.parse.text.match
import kotlin.test.Test

class MatchTest : AbstractParseTest() {
    @Test
    fun `matches char literal and produces matching input`() {
        val parser = match(literal("abc", 1))

        parser.expecting {
            expectMatch {
                expectLiteral("abc")
            }
        }

        parser.matches("abc", expected = "abc")

        parser.doesNotMatch("") {
            expectLiteral("abc")
        }

        parser.doesNotMatch("aX") {
            expectLiteral("abc")
        }

        parser.doesNotMatch("abcX") {
            failAt(3)
            expectEndOfInput()
        }
    }

    @Test
    fun `matches byte literal and produces matching input`() {
        val parser = net.rubygrapefruit.parse.binary.match(literal(byteArrayOf(0x1, 0x2), 1))

        parser.expecting {
            expectMatch {
                expectLiteral(0x1, 0x2)
            }
        }

        parser.matches(0x1, 0x2, expected = byteArrayOf(0x1, 0x2))

        // missing
        parser.doesNotMatch {
            expectLiteral(0x1)
        }
        parser.doesNotMatch(0x1) {
            failAt(1)
            expectLiteral(0x2)
        }
    }
}