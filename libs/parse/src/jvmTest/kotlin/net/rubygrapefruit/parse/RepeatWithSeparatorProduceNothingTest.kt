package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.repeat
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class RepeatWithSeparatorProduceNothingTest : AbstractParseTest() {
    @Test
    fun `matches n char literals with separator`() {
        val parser = repeat(3, literal("ab"), literal(",", 2))

        parser.expecting {
            expectRepeat(3, hasResult = false) {
                expectLiteral("ab")
                expectLiteral(",")
            }
        }

        parser.matches("ab,ab,ab")

        // missing
        parser.doesNotMatch("") {
            expectLiteral("ab")
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
}