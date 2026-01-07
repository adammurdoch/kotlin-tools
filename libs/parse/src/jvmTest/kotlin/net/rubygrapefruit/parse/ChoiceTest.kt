package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.char.literal
import net.rubygrapefruit.parse.combinators.oneOf
import kotlin.test.Test

class ChoiceTest : AbstractParseTest() {
    @Test
    fun `matches literals`() {
        val parser = oneOf(literal("abc", 1), literal("12", 2))

        parser.matches("abc", expected = 1)
        parser.matches("12", expected = 2)

        // missing
        parser.doesNotMatch("") {
            expect("\"abc\"")
            expect("\"12\"")
        }
        parser.doesNotMatch("ab") {
            expect("\"abc\"")
            expect("\"12\"")
        }
        parser.doesNotMatch("1") {
            expect("\"abc\"")
            expect("\"12\"")
        }

        // extra
        parser.doesNotMatch("abcX") {
            failAt(3)
            expectEndOfInput()
        }
        parser.doesNotMatch("12X") {
            failAt(2)
            expectEndOfInput()
        }
    }

    @Test
    fun `matches literals with common prefix`() {
        val parser = oneOf(literal("abc", 1), literal("abd", 2))

        parser.matches("abc", expected = 1)
        parser.matches("abd", expected = 2)

        parser.doesNotMatch("ab") {
            expect("\"abc\"")
            expect("\"abd\"")
        }
    }

    @Test
    fun `uses result from first parser that matches`() {
        val parser = oneOf(literal("ab", 1), literal("abc", 2))

        parser.matches("ab", expected = 1)

        parser.doesNotMatch("abc") {
            failAt(2)
            expectEndOfInput()
        }
    }
}