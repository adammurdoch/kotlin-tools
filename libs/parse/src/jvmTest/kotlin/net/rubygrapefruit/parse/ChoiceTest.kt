package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.char.literal
import net.rubygrapefruit.parse.combinators.oneOf
import kotlin.test.Test

class ChoiceTest : AbstractParseTest() {
    @Test
    fun `matches literals`() {
        val parser = oneOf(literal("abc", 1), literal("12", 2))

        matches(parser, "abc", expected = 1)
        matches(parser, "12", expected = 2)

        // missing
        doesNotMatch(parser, "") {
            expect("\"abc\"")
            expect("\"12\"")
        }
        doesNotMatch(parser, "ab") {
            expect("\"abc\"")
            expect("\"12\"")
        }
        doesNotMatch(parser, "1") {
            expect("\"abc\"")
            expect("\"12\"")
        }

        // extra
        doesNotMatch(parser, "abcX") {
            failAt(3)
            expectEndOfInput()
        }
        doesNotMatch(parser, "12X") {
            failAt(2)
            expectEndOfInput()
        }
    }

    @Test
    fun `matches literals with common prefix`() {
        val parser = oneOf(literal("abc", 1), literal("abd", 2))

        matches(parser, "abc", expected = 1)
        matches(parser, "abd", expected = 2)

        doesNotMatch(parser, "ab") {
            expect("\"abc\"")
            expect("\"abd\"")
        }
    }

    @Test
    fun `uses result from first parser that matches`() {
        val parser = oneOf(literal("ab", 1), literal("abc", 2))

        matches(parser, "ab", expected = 1)

        doesNotMatch(parser, "abc") {
            failAt(2)
            expectEndOfInput()
        }
    }
}