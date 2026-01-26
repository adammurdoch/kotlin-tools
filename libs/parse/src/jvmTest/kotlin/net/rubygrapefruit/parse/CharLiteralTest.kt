package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class CharLiteralTest : AbstractParseTest() {
    @Test
    fun `matches single char literal`() {
        val parser = literal("a")

        parser.expecting {
            expectLiteral("a")
        }

        parser.matches("a")

        // missing
        parser.doesNotMatch("") {
            // don't use expectLiteral() here, to check formatting
            expect("\"a\"")
        }

        // unexpected char
        parser.doesNotMatch("X") {
            expect("\"a\"")
        }

        // extra char
        parser.doesNotMatch("aX") {
            failAt(1)
            expectEndOfInput()
        }
        parser.doesNotMatch("aXXX") {
            failAt(1)
            expectEndOfInput()
        }

        // incorrect case
        parser.doesNotMatch("A") {
            expect("\"a\"")
        }
    }

    @Test
    fun `matches single new line character`() {
        val parser = literal("\n")

        parser.expecting {
            expectLiteral("\n")
        }

        parser.matches("\n")

        // missing
        parser.doesNotMatch("") {
            // don't use expectLiteral() here, to check formatting
            expect("new line")
        }

        // unexpected char
        parser.doesNotMatch("X") {
            expect("new line")
        }

        // extra char
        parser.doesNotMatch("\nX") {
            failAt(1, 2, 1)
            expectContext("", "X")
            expectEndOfInput()
        }
    }

    @Test
    fun `matches single char literal and produces result`() {
        val parser = literal("a", 1)

        parser.expecting {
            expectLiteral("a", result = 1)
        }

        parser.matches("a", expected = 1)
    }

    @Test
    fun `matches multi-char literal`() {
        val parser = literal("ab")

        parser.expecting {
            expectLiteral("ab")
        }

        parser.matches("ab")

        // missing
        parser.doesNotMatch("") {
            // don't use expectLiteral() here, to check formatting
            expect("\"ab\"")
        }
        parser.doesNotMatch("a") {
            expect("\"ab\"")
        }
        parser.doesNotMatch("b") {
            expect("\"ab\"")
        }

        // unexpected char
        parser.doesNotMatch("X") {
            expect("\"ab\"")
        }
        parser.doesNotMatch("aX") {
            expect("\"ab\"")
        }
        parser.doesNotMatch("Xb") {
            expect("\"ab\"")
        }
        parser.doesNotMatch("Xab") {
            expect("\"ab\"")
        }
        parser.doesNotMatch("aXb") {
            expect("\"ab\"")
        }

        // extra char
        parser.doesNotMatch("abX") {
            failAt(2)
            expectEndOfInput()
        }
        parser.doesNotMatch("abXX") {
            failAt(2)
            expectEndOfInput()
        }

        // incorrect case
        parser.doesNotMatch("AB") {
            expect("\"ab\"")
        }
        parser.doesNotMatch("Ab") {
            expect("\"ab\"")
        }
        parser.doesNotMatch("aB") {
            expect("\"ab\"")
        }
    }

    @Test
    fun `matches multi-char literal and produces result`() {
        val parser = literal("ab", 1)

        parser.matches("ab", expected = 1)
    }
}