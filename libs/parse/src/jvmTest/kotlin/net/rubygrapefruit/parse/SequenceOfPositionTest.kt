package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.discard
import net.rubygrapefruit.parse.combinators.sequence
import net.rubygrapefruit.parse.combinators.zeroOrMore
import net.rubygrapefruit.parse.general.position
import net.rubygrapefruit.parse.text.digit
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class SequenceOfPositionTest : AbstractParseTest() {
    @Test
    fun `matches zero or more followed by position followed by literal`() {
        val parser = sequence(
            discard(zeroOrMore(digit())),
            position(),
            literal("!")
        )

        parser.matches("!", expected = Position.Zero)
        parser.matches("123!", expected = Position(3))

        // missing
        parser.doesNotMatch("") {
            expect("a digit")
            expectLiteral("!")
        }
        parser.doesNotMatch("12") {
            failAt(2)
            expect("a digit")
            expectLiteral("!")
        }

        // unexpected
        parser.doesNotMatch("12X") {
            failAt(2)
            expect("a digit")
            expectLiteral("!")
        }

        // extra
        parser.doesNotMatch("1!X") {
            failAt(2)
            expectEndOfInput()
        }
    }
}