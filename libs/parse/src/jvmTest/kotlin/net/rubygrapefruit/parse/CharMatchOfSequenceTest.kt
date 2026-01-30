package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.sequence
import net.rubygrapefruit.parse.text.literal
import net.rubygrapefruit.parse.text.match
import net.rubygrapefruit.parse.text.oneOf
import kotlin.test.Test

class CharMatchOfSequenceTest : AbstractParseTest() {
    @Test
    fun `matches sequence of literal then one of char`() {
        val parser = match(
            sequence(
                literal("12"),
                oneOf('3', '!')
            ) { _, b -> b.code + 2 }
        )

        parser.expecting {
            expectMatch {
                expectSequence {
                    expectLiteral("12")
                    expectOneOf('3', '!', hasResult = false)
                }
            }
        }

        parser.matches("123", expected = "123")
        parser.matches("12!", expected = "12!")

        // missing
        parser.doesNotMatch("") {
            expectLiteral("12")
        }
        parser.doesNotMatch("12") {
            failAt(2)
            expectLiteral("!")
            expectLiteral("3")
        }

        // extra
        parser.doesNotMatch("123!") {
            failAt(3)
            expectEndOfInput()
        }
    }
}