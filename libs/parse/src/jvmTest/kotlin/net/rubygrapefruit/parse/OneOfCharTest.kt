package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.text.oneOf
import kotlin.test.Test
import kotlin.test.fail

class OneOfCharTest : AbstractParseTest() {
    @Test
    fun `matches one of several chars`() {
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

    @Test
    fun `matches one of a range of chars`() {
        val parser = oneOf('a'..'c')

        parser.expecting {
            expectOneOf('a', 'b', 'c')
        }

        parser.matches("a", expected = 'a')
        parser.matches("b", expected = 'b')
        parser.matches("c", expected = 'c')

        // missing
        parser.doesNotMatch("") {
            expectLiteral("a")
            expectLiteral("b")
            expectLiteral("c")
        }

        // unexpected
        parser.doesNotMatch("X") {
            expectLiteral("a")
            expectLiteral("b")
            expectLiteral("c")
        }

        // mismatched case
        parser.doesNotMatch("A") {
            expectLiteral("a")
            expectLiteral("b")
            expectLiteral("c")
        }

        // extra
        parser.doesNotMatch("cX") {
            failAt(1)
            expectEndOfInput()
        }
        parser.doesNotMatch("cb") {
            failAt(1)
            expectEndOfInput()
        }
    }

    @Test
    fun `matches one of a collection of chars`() {
        val parser = oneOf(setOf('a', 'b'))

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
    fun `ignores duplicate chars`() {
        val parser = oneOf('a', 'b', 'b', 'a')

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
    }

    @Test
    fun `cannot provide single char`() {
        try {
            oneOf(listOf('a'))
        } catch (e: IllegalArgumentException) {
            return
        }
        fail()
    }

    @Test
    fun `cannot provide no chars`() {
        try {
            oneOf(emptyList<Char>())
        } catch (e: IllegalArgumentException) {
            return
        }
        fail()
    }
}