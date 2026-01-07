package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.char.literal
import kotlin.test.Test

class CharLiteralTest : AbstractParseTest() {
    @Test
    fun `matches single char literal`() {
        val parser = literal("a")

        parser.matches("a")

        // missing
        parser.doesNotMatch("") {
            expectLiteral("a")
        }

        // unexpected char
        parser.doesNotMatch("X") {
            expectLiteral("a")
        }

        // extra char
        parser.doesNotMatch("aX") {
            failAt(1)
            expectEndOfInput()
        }

        // incorrect case
        parser.doesNotMatch("A") {
            expectLiteral("a")
        }
    }

    @Test
    fun `matches single char literal and produces result`() {
        val parser = literal("a", 1)

        parser.matches("a", expected = 1)
    }

    @Test
    fun `matches multi-char literal`() {
        val parser = literal("ab")

        parser.matches("ab")

        // missing
        parser.doesNotMatch("") {
            expectLiteral("ab")
        }
        parser.doesNotMatch("a") {
            expectLiteral("ab")
        }
        parser.doesNotMatch("b") {
            expectLiteral("ab")
        }

        // unexpected char
        parser.doesNotMatch("X") {
            expectLiteral("ab")
        }
        parser.doesNotMatch("aX") {
            expectLiteral("ab")
        }
        parser.doesNotMatch("Xb") {
            expectLiteral("ab")
        }
        parser.doesNotMatch("Xab") {
            expectLiteral("ab")
        }
        parser.doesNotMatch("aXb") {
            expectLiteral("ab")
        }

        // extra char
        parser.doesNotMatch("abX") {
            failAt(2)
            expectEndOfInput()
        }

        // incorrect case
        parser.doesNotMatch("AB") {
            expectLiteral("ab")
        }
        parser.doesNotMatch("Ab") {
            expectLiteral("ab")
        }
        parser.doesNotMatch("aB") {
            expectLiteral("ab")
        }
    }

    @Test
    fun `matches multi-char literal and produces result`() {
        val parser = literal("ab", 1)

        parser.matches("ab", expected = 1)
    }
}