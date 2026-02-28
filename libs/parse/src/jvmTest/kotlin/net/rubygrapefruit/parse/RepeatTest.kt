package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.repeat
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class RepeatTest : AbstractParseTest() {
    @Test
    fun `parses n char literals`() {
        val parser = repeat(4, literal("a", 1))

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
        parser.doesNotMatch("aaaaXX") {
            failAt(4)
            expectEndOfInput()
        }
    }
}