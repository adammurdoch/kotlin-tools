package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.char.literal
import kotlin.test.Test

class CharLiteralTest : AbstractParseTest() {
    @Test
    fun `matches single char literal`() {
        val parser = literal("a")

        matches(parser, "a")

        // missing
        doesNotMatch(parser, "") {
            expect("\"a\"")
        }

        // unexpected char
        doesNotMatch(parser, "X") {
            expect("\"a\"")
        }

        // extra char
        doesNotMatch(parser, "aX") {
            failAt(1)
            expectEndOfInput()
        }

        // incorrect case
        doesNotMatch(parser, "A") {
            expect("\"a\"")
        }
    }

    @Test
    fun `matches single char literal and produces result`() {
        val parser = literal("a", 1)

        matches(parser, "a", 1)
    }

    @Test
    fun `matches multi-char literal`() {
        val parser = literal("ab")

        matches(parser, "ab")

        // missing
        doesNotMatch(parser, "") {
            expect("\"ab\"")
        }
        doesNotMatch(parser, "a") {
            expect("\"ab\"")
        }
        doesNotMatch(parser, "b") {
            expect("\"ab\"")
        }

        // unexpected char
        doesNotMatch(parser, "X") {
            expect("\"ab\"")
        }
        doesNotMatch(parser, "aX") {
            expect("\"ab\"")
        }
        doesNotMatch(parser, "Xb") {
            expect("\"ab\"")
        }
        doesNotMatch(parser, "Xab") {
            expect("\"ab\"")
        }
        doesNotMatch(parser, "aXb") {
            expect("\"ab\"")
        }

        // extra char
        doesNotMatch(parser, "abX") {
            failAt(2)
            expectEndOfInput()
        }

        // incorrect case
        doesNotMatch(parser, "AB") {
            expect("\"ab\"")
        }
        doesNotMatch(parser, "Ab") {
            expect("\"ab\"")
        }
        doesNotMatch(parser, "aB") {
            expect("\"ab\"")
        }
    }

    @Test
    fun `matches multi-char literal and produces result`() {
        val parser = literal("ab", 1)

        matches(parser, "ab", 1)
    }
}