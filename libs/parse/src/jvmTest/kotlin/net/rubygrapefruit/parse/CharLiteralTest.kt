package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.char.literal
import kotlin.test.Test

class CharLiteralTest : AbstractParseTest() {
    @Test
    fun `matches single char literal`() {
        val parser = literal("a")

        matches(parser, "a")

        // missing
        doesNotMatch(parser, "")

        // unexpected char
        doesNotMatch(parser, "X")

        // extra char
        doesNotMatch(parser, "aX")

        // incorrect case
        doesNotMatch(parser, "A")
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
        doesNotMatch(parser, "")
        doesNotMatch(parser, "a")
        doesNotMatch(parser, "b")

        // unexpected char
        doesNotMatch(parser, "X")
        doesNotMatch(parser, "aX")
        doesNotMatch(parser, "Xb")
        doesNotMatch(parser, "Xab")
        doesNotMatch(parser, "aXb")

        // extra char
        doesNotMatch(parser, "abX")

        // incorrect case
        doesNotMatch(parser, "AB")
        doesNotMatch(parser, "Ab")
        doesNotMatch(parser, "aB")
    }

    @Test
    fun `matches multi-char literal and produces result`() {
        val parser = literal("ab", 1)

        matches(parser, "ab", 1)
    }
}