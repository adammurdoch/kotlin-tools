package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.literal
import net.rubygrapefruit.parse.combinators.MappingResult
import net.rubygrapefruit.parse.combinators.check
import net.rubygrapefruit.parse.text.literal
import net.rubygrapefruit.parse.text.parse
import kotlin.test.Test

class CheckTest : AbstractParseTest() {
    @Test
    fun `matches char literal and applies mapping function`() {
        val parser = check(literal("abc", 1)) { MappingResult.of("[$it]") }

        parser.matches("abc", expected = "[1]")

        // missing
        parser.doesNotMatch("") {
            expectLiteral("abc")
        }

        // unexpected char
        parser.doesNotMatch("aX") {
            expectLiteral("abc")
        }

        // extra char
        parser.doesNotMatch("abcX") {
            failAt(3)
            expectEndOfInput()
        }
    }

    @Test
    fun `matches byte literal and applies mapping function`() {
        val parser = check(literal(byteArrayOf(0x1, 0x2), 1)) { MappingResult.of("[$it]") }

        parser.matches(0x1, 0x2, expected = "[1]")

        // missing
        parser.doesNotMatch {
            expectLiteral(0x1)
        }

        // unexpected
        parser.doesNotMatch(0x1, 0x3) {
            failAt(1)
            expectLiteral(0x2)
        }

        // extra
        parser.doesNotMatch(0x1, 0x2, 0x3) {
            failAt(2)
            expectEndOfInput()
        }
    }

    @Test
    fun `mapping function can reject value`() {
        val parser = check(literal("abc", 1)) { MappingResult.expected("not $it") }

        parser.doesNotMatch("abc") {
            expect("not 1")
        }

        // missing
        parser.doesNotMatch("") {
            expectLiteral("abc")
        }
    }

    @Test
    fun `rethrows mapping failure`() {
        val failure = RuntimeException()
        val parser = check<_, _, String>(literal("abc")) {
            failure.fillInStackTrace()
            throw failure
        }

        failsWith(failure) {
            parser.parse("abc")
        }
    }

}