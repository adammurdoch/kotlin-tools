package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.sequence
import net.rubygrapefruit.parse.text.literal
import net.rubygrapefruit.parse.text.match
import net.rubygrapefruit.parse.text.oneOf
import kotlin.test.Test

class CharMatchOfSequenceTest : AbstractParseTest() {
    @Test
    fun `matches sequence of literal then one of char`() {
        val parser = match(
            sequence(
                literal("12"),
                oneOf('3', '!')
            ) { _, b -> b.code + 2 }
        )

        parser.expecting {
            expectLiteral("12")
        }

        parser.matches("123", expected = "123")
        parser.matches("12!", expected = "12!")

        // extra
        parser.doesNotMatch("123!") {
            failAt(3)
            expectEndOfInput()
        }
    }
}