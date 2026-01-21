package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.text.literal
import net.rubygrapefruit.parse.combinators.zeroOrMore
import kotlin.test.Test

class ZeroOrMoreTest : AbstractParseTest() {
    @Test
    fun `matches zero or more char literals`() {
        val parser = zeroOrMore(literal("abc", 1))

        parser.expecting {
            emptyMatch()
            expectLiteral("abc")
            expectIsChoice(2)
        }

        parser.matches("", expected = emptyList())
        parser.matches("abc", expected = listOf(1))
        parser.matches("abcabc", expected = listOf(1, 1))

        // missing
        parser.doesNotMatch("a") {
            expectLiteral("abc")
            expectEndOfInput()
        }
        parser.doesNotMatch("ab") {
            expectLiteral("abc")
            expectEndOfInput()
        }
        parser.doesNotMatch("abca") {
            failAt(3)
            expectLiteral("abc")
            expectEndOfInput()
        }

        // unexpected
        parser.doesNotMatch("X") {
            expectLiteral("abc")
            expectEndOfInput()
        }
        parser.doesNotMatch("aX") {
            expectLiteral("abc")
            expectEndOfInput()
        }
        parser.doesNotMatch("abcaX") {
            failAt(3)
            expectLiteral("abc")
            expectEndOfInput()
        }

        // extra
        parser.doesNotMatch("abcX") {
            failAt(3)
            expectLiteral("abc")
            expectEndOfInput()
        }
    }
}