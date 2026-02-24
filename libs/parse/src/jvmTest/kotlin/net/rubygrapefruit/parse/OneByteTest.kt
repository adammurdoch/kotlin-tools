package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.one
import kotlin.test.Test

class OneByteTest : AbstractParseTest() {
    @Test
    fun `matches one byte`() {
        val parser = one()

        parser.expecting {
            expectOneByte()
        }

        parser.matches(0x1, expected = 0x1) {
            steps {
                commit(1)
            }
        }
        parser.matches(0xFF.toByte(), expected = 0xFF.toByte())

        // missing
        parser.doesNotMatch {
            expect("any byte")
        }

        // extra
        parser.doesNotMatch(0x1, 0x1) {
            failAt(1)
            expectEndOfInput()
        }
    }
}