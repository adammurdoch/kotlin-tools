package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.text.oneOf
import kotlin.test.Test

class OneOfCharTest : AbstractParseTest() {
    @Test
    fun `matches one of a set of chars`() {
        val parser = oneOf('a', 'b')

        parser.expecting {
            expectOneOf('a', 'b')
        }

        parser.matches("a", expected = 'a')
        parser.matches("b", expected = 'b')

        // missing
        parser.doesNotMatch("") {
            expectLiteral("a")
            expectLiteral("b")
        }

        // unexpected
        parser.doesNotMatch("X") {
            expectLiteral("a")
            expectLiteral("b")
        }

        // mismatched case
        parser.doesNotMatch("A") {
            expectLiteral("a")
            expectLiteral("b")
        }

        // extra
        parser.doesNotMatch("bX") {
            failAt(1)
            expectEndOfInput()
        }
        parser.doesNotMatch("ab") {
            failAt(1)
            expectEndOfInput()
        }
    }

    @Test
    fun `matches new line character`() {
        val parser = oneOf(';', '\n')

        parser.expecting {
            expectOneOf(';', '\n')
        }

        parser.matches(";", expected = ';')
        parser.matches("\n", expected = '\n')

        // missing
        parser.doesNotMatch("") {
            expectLiteral(";")
            expect("new line")
        }

        // extra
        parser.doesNotMatch("\nX") {
            failAt(1, 2, 1)
            expectContext("", "X")
            expectEndOfInput()
        }
    }
}