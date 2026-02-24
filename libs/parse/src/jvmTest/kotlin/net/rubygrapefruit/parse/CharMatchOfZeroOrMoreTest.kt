package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.zeroOrMore
import net.rubygrapefruit.parse.text.literal
import net.rubygrapefruit.parse.text.match
import net.rubygrapefruit.parse.text.oneOf
import kotlin.test.Test

class CharMatchOfZeroOrMoreTest : AbstractParseTest() {
    @Test
    fun `matches zero or more one char literal`() {
        val parser = match(zeroOrMore(literal("12", 1)))

        parser.expecting {
            expectMatch {
                expectZeroOrMore(hasResult = false) {
                    expectLiteral("12")
                }
            }
        }

        parser.matches("", expected = "")
        parser.matches("12", expected = "12")
        parser.matches("1212", expected = "1212")

        // unexpected
        parser.doesNotMatch("X") {
            expectLiteral("12")
            expectEndOfInput()
        }
        parser.doesNotMatch("1X") {
            expectLiteral("12")
            expectEndOfInput()
        }
        parser.doesNotMatch("12X") {
            failAt(2)
            expectLiteral("12")
            expectEndOfInput()
        }
    }

    @Test
    fun `matches zero or more one of char`() {
        val parser = match(zeroOrMore(oneOf('1', '2')))

        parser.expecting {
            expectMatch {
                expectZeroOrMoreSingleInput(hasResult = false) {
                    expectOneOf("1", "2")
                }
            }
        }

        parser.matches("", expected = "")
        parser.matches("1", expected = "1")
        parser.matches("2", expected = "2")
        parser.matches("2211", expected = "2211")

        // unexpected
        parser.doesNotMatch("X") {
            expectLiteral("1")
            expectLiteral("2")
            expectEndOfInput()
        }
        parser.doesNotMatch("1X") {
            failAt(1)
            expectLiteral("1")
            expectLiteral("2")
            expectEndOfInput()
        }
    }
}