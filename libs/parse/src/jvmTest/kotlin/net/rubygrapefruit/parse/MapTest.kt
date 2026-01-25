package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.literal
import net.rubygrapefruit.parse.combinators.map
import kotlin.test.Test

class MapTest : AbstractParseTest() {
    @Test
    fun `maps the result of binary literal`() {
        val parser = map(literal(byteArrayOf(0x1, 0x2), 1)) { it + 1 }

        parser.matches(0x1, 0x2, expected = 2)

        // missing
        parser.doesNotMatch() {
            expectLiteral(0x1)
        }
        parser.doesNotMatch(0x1) {
            failAt(1)
            expectLiteral(0x2)
        }

        // extra
        parser.doesNotMatch(0x1, 0x2, 0x3) {
            failAt(2)
            expectEndOfInput()
        }
    }
}