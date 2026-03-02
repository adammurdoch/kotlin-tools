package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.oneOf
import net.rubygrapefruit.parse.combinators.repeat
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class ChoiceOfRepeatTest : AbstractParseTest() {
    @Test
    fun `matches choice of repeated char literals`() {
        val parser = oneOf(
            repeat(3, literal("a", 1)),
            repeat(2, literal("a", 2))
        )

        parser.expecting {
            expectChoice {
                expectRepeat(3) {
                    expectLiteral("a", result = 1)
                }
                expectRepeat(2) {
                    expectLiteral("a", result = 2)
                }
            }
        }

        parser.matches("aaa", expected = listOf(1, 1, 1))
        parser.matches("aa", expected = listOf(2, 2))

        // missing
        parser.doesNotMatch("a") {
            failAt(1)
            expectLiteral("a")
        }

        // unexpected
        parser.doesNotMatch("X") {
            expectLiteral("a")
        }
        parser.doesNotMatch("aX") {
            failAt(1)
            expectLiteral("a")
        }

        // extra
        parser.doesNotMatch("aaaa") {
            failAt(3)
            expectEndOfInput()
        }
        parser.doesNotMatch("aaX") {
            failAt(2)
            expectLiteral("a")
            expectEndOfInput()
        }
    }
}