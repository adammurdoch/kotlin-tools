package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.one
import net.rubygrapefruit.parse.combinators.oneOrMore
import kotlin.test.Test

class OneOrMoreOfOneByteTest : AbstractParseTest() {
    /**
     * NOTE: this is in a separate class to the text equivalent because importing both `one()` overloads in the same class is painful.
     */
    @Test
    fun `matches one or more of one byte`() {
        val parser = oneOrMore(one())

        parser.expecting {
            expectOneOrMoreSingleInput {
                expectOneByte()
            }
        }

        parser.matches(0x1, expected = bytes(0x1)) {
            steps {
                advance(1)
            }
        }
        parser.matches(0x1, 0x2, 0x3, expected = bytes(0x1, 0x2, 0x3)) {
            steps {
                advance(3)
            }
        }

        // missing
        parser.doesNotMatch {
            expectOneByte()
            steps {}
        }
    }
}