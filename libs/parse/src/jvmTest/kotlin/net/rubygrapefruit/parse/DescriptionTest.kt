package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.literal
import net.rubygrapefruit.parse.combinators.describedAs
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class DescriptionTest : AbstractParseTest() {
    @Test
    fun `replaces expectation of char literal`() {
        val parser = describedAs(literal("abc", 1), "<literal>")

        parser.matches("abc", expected = 1)

        // missing
        parser.doesNotMatch("") {
            expect("<literal>")
        }
        parser.doesNotMatch("a") {
            expect("<literal>")
        }

        // unexpected
        parser.doesNotMatch("X") {
            expect("<literal>")
        }

        // extra
        parser.doesNotMatch("abcX") {
            failAt(3)
            expectEndOfInput()
        }
    }

    @Test
    fun `replaces expectation at start of boolean literal`() {
        val parser = describedAs(literal(byteArrayOf(0x1, 0x2), 1), "<literal>")

        parser.matches(0x1, 0x2, expected = 1)

        // missing
        parser.doesNotMatch {
            expect("<literal>")
        }
        parser.doesNotMatch(0x1) {
            failAt(1)
            expectLiteral(0x2)
        }
    }
}