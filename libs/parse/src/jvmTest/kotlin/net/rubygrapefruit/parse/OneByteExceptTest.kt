package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.literal
import net.rubygrapefruit.parse.binary.oneExcept
import kotlin.test.Test

class OneByteExceptTest : AbstractParseTest() {
    @Test
    fun `matches any byte except single byte literal`() {
        val parser = oneExcept(literal(byteArrayOf(0)))

        parser.expecting {
            expectSequence {
                expectNot {
                    expectLiteral(0)
                }
                expectOneByte()
            }
        }

        parser.matches(0x1, expected = 0x1)

        // missing
        parser.doesNotMatch {
            expect("any byte")
            expect("not x0")
        }

        // matches predicate
        parser.doesNotMatch(0) {
            expect("any byte")
            expect("not x0")
        }

        // extra
        parser.doesNotMatch(0x1, 0x2) {
            failAt(1)
            expectEndOfInput()
        }
    }
}