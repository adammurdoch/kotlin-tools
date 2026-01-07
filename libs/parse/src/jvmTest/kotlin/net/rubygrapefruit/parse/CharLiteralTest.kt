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

        // incorrect case
        parser.doesNotMatch("A") {
            expect("\"a\"")
        }
    }

    @Test
    fun `matches single char literal and produces result`() {
        val parser = literal("a", 1)

        parser.matches("a", 1)
    }

    @Test
    fun `matches multi-char literal`() {
        val parser = literal("ab")

        parser.matches("ab")

        // missing
        parser.doesNotMatch("") {
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

        parser.matches("ab", 1)
    }
}