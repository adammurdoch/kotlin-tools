package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.literal
import net.rubygrapefruit.parse.combinators.not
import kotlin.test.Test

class NotTest : AbstractParseTest() {
    @Test
    fun `matches nothing`() {
        val parser = not(literal(byteArrayOf(0x1)))

        parser.expecting {
            expectNot {
                expectLiteral(0x1)
            }
        }

        parser.matches {
            steps {
                commit(0)
            }
        }

        // matches predicate
        parser.doesNotMatch(0x1) {
            expectEndOfInput()
            expect("not x01")
            steps { }
        }
        parser.doesNotMatch(0x1, 0x2) {
            expectEndOfInput()
            expect("not x01")
            steps { }
        }

        // extra
        parser.doesNotMatch(0x2) {
            expectEndOfInput()
            expect("not x01")
            steps {
                commit(0)
            }
        }
    }
}