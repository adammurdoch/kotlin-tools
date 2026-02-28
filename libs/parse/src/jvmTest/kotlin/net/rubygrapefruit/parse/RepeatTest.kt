package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.repeat
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class RepeatTest : AbstractParseTest() {
    @Test
    fun `parses n char literals`() {
        val parser = repeat(4, literal("a", 1))

        parser.expecting {
            expectRepeat(4) {
                expectLiteral("a", result = 1)
            }
        }

        parser.matches("aaaa", listOf(1, 1, 1, 1))

        // missing
        parser.doesNotMatch("") {
            expectLiteral("a")
        }
        parser.doesNotMatch("aaa") {
            failAt(3)
            expectLiteral("a")
        }

        // unexpected
        parser.doesNotMatch("X") {
            expectLiteral("a")
        }
        parser.doesNotMatch("aaXa") {
            failAt(2)
            expectLiteral("a")
        }

        // extra
        parser.doesNotMatch("aaaaa") {
            failAt(4)
            expectEndOfInput()
        }
        parser.doesNotMatch("aaaaXX") {
            failAt(4)
            expectEndOfInput()
        }
    }

    @Test
    fun `can match zero instances`() {
        val parser = repeat(0, literal("abc", 2))

        parser.expecting {
            expectSucceed(result = emptyList<Int>())
        }

        parser.matches("", expected = listOf())

        // unexpected
        parser.doesNotMatch("X") {
            expectEndOfInput()
        }

        // extra
        parser.doesNotMatch("abc") {
            expectEndOfInput()
        }
    }
}