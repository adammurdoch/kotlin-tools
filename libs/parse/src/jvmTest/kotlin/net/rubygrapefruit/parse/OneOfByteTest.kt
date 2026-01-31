package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.oneOf
import kotlin.test.Test
import kotlin.test.fail

class OneOfByteTest : AbstractParseTest() {
    @Test
    fun `matches one of several bytes`() {
        val parser = oneOf(0x1, 0x2)

        parser.expecting {
            expectOneOf(0x1, 0x2)
        }

        parser.matches(0x1, expected = 0x1)
        parser.matches(0x2, expected = 0x2)

        // missing
        parser.doesNotMatch {
            expectLiteral(0x1)
            expectLiteral(0x2)
        }

        // unexpected
        parser.doesNotMatch(0x11) {
            expectLiteral(0x1)
            expectLiteral(0x2)
        }

        // extra
        parser.doesNotMatch(0x1, 0x1) {
            failAt(1)
            expectEndOfInput()
        }
    }

    @Test
    fun `matches one of a range of bytes`() {
        val parser = oneOf(0x1, 0x3)

        parser.expecting {
            expectOneOf(0x1, 0x2, 0x3)
        }

        parser.matches(0x1, expected = 0x1)
        parser.matches(0x2, expected = 0x2)
        parser.matches(0x3, expected = 0x3)

        // missing
        parser.doesNotMatch {
            expectLiteral(0x1)
            expectLiteral(0x2)
            expectLiteral(0x3)
        }

        // unexpected
        parser.doesNotMatch(0x11) {
            expectLiteral(0x1)
            expectLiteral(0x2)
            expectLiteral(0x3)
        }

        // extra
        parser.doesNotMatch(0x1, 0x1) {
            failAt(1)
            expectEndOfInput()
        }
    }

    @Test
    fun `matches one of a collection of bytes`() {
        val parser = oneOf(setOf(0x1, 0x2))

        parser.expecting {
            expectOneOf(0x1, 0x2)
        }

        parser.matches(0x1, expected = 0x1)
        parser.matches(0x2, expected = 0x2)

        // missing
        parser.doesNotMatch {
            expectLiteral(0x1)
            expectLiteral(0x2)
        }

        // unexpected
        parser.doesNotMatch(0x11) {
            expectLiteral(0x1)
            expectLiteral(0x2)
        }

        // extra
        parser.doesNotMatch(0x1, 0x1) {
            failAt(1)
            expectEndOfInput()
        }
    }

    @Test
    fun `ignores duplicate bytes`() {
        val parser = oneOf(0x1, 0x2, 0x1, 0x2)

        parser.expecting {
            expectOneOf(0x1, 0x2)
        }

        parser.matches(0x1, expected = 0x1)
        parser.matches(0x2, expected = 0x2)

        // missing
        parser.doesNotMatch {
            expectLiteral(0x1)
            expectLiteral(0x2)
        }
    }

    @Test
    fun `cannot provide list with single byte`() {
        try {
            oneOf(listOf(0x1))
        } catch (e: IllegalArgumentException) {
            return
        }
        fail()
    }

    @Test
    fun `cannot provide empty list`() {
        try {
            oneOf(emptyList<Byte>()) // type parameter to force correct overload
        } catch (e: IllegalArgumentException) {
            return
        }
        fail()
    }

}