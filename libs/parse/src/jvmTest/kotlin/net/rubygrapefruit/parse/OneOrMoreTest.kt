package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.oneOrMore
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class OneOrMoreTest : AbstractParseTest() {
    @Test
    fun `matches one or more char literals`() {
        val parser = oneOrMore(literal("a.", 1))

        parser.expecting {
            expectOneOrMore {
                expectLiteral("a.", result = 1)
            }
        }

        parser.matches("a.", expected = listOf(1))
        parser.matches("a.a.a.", expected = listOf(1, 1, 1))

        // missing
        parser.doesNotMatch("") {
            expectLiteral("a.")
        }
        parser.doesNotMatch("a") {
            expectLiteral("a.")
        }
        parser.doesNotMatch("a.a") {
            failAt(2)
            expectLiteral("a.")
            expectEndOfInput()
        }

        // unexpected
        parser.doesNotMatch("X") {
            expectLiteral("a.")
        }
        parser.doesNotMatch("aX") {
            expectLiteral("a.")
        }
        parser.doesNotMatch("a.X") {
            failAt(2)
            expectLiteral("a.")
            expectEndOfInput()
        }
    }
}
