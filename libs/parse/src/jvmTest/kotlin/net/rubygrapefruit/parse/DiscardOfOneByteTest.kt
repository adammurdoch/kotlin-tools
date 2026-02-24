package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.one
import net.rubygrapefruit.parse.combinators.discard
import kotlin.test.Test

class DiscardOfOneByteTest : AbstractParseTest() {
    @Test
    fun `discards the result of one byte`() {
        val parser = discard(one())

        parser.expecting {
            expectOneByte(hasResult = false)
        }

        parser.matches(0x1)
        parser.matches(0x45)

        parser.doesNotMatch {
            expect("any byte")
        }
    }
}