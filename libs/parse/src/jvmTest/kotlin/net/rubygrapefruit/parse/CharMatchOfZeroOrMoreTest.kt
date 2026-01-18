package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.zeroOrMore
import net.rubygrapefruit.parse.text.match
import net.rubygrapefruit.parse.text.oneOf
import kotlin.test.Test

class CharMatchOfZeroOrMoreTest : AbstractParseTest() {
    @Test
    fun `matches zero or more one of char`() {
        val parser = match(zeroOrMore(oneOf('1', '2')))

        parser.expecting {
            emptyMatch()
            expectLiteral("1")
            expectLiteral("2")
        }

        parser.matches("", expected = "")
        parser.matches("1", expected = "1")
        parser.matches("2", expected = "2")
        parser.matches("2211", expected = "2211")

        // unexpected
        parser.doesNotMatch("X") {
            expectLiteral("1")
            expectLiteral("2")
            expectEndOfInput()
        }
    }
}