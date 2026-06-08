package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.repeat
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class RepeatWithSeparatorTest : AbstractParseTest() {
    @Test
    fun `matches n char literals with separator`() {
        val parser = repeat(3, literal("ab", 1), literal(",", 2))

        parser.expecting {
            expectRepeat(3) {
                expectLiteral("ab", result = 1)
                expectLiteral(",")
            }
        }

        parser.matches("ab,ab,ab", expected = listOf(1, 1, 1))

        // missing
        parser.doesNotMatch("") {
            expectLiteral("ab")
        }
        parser.doesNotMatch("ab") {
            failAt(2)
            expectLiteral(",")
        }
        parser.doesNotMatch("ab,ab") {
            failAt(5)
            expectLiteral(",")
        }
        parser.doesNotMatch("ab,ab,") {
            failAt(6)
            expectLiteral("ab")
        }

        // prefix missing
        parser.doesNotMatch(",ab") {
            expectLiteral("ab")
        }

        // separator missing
        parser.doesNotMatch("abab") {
            failAt(2)
            expectLiteral(",")
        }

        // extra
        parser.doesNotMatch("ab,ab,abX") {
            failAt(8)
            expectEndOfInput()
        }
    }

    @Test
    fun `can match 0 times`() {
        val parser = repeat(0, literal("ab", 1), literal(",", 2))

        parser.matches("", expected = emptyList())

        parser.doesNotMatch("ab") {
            expectEndOfInput()
        }
    }
}