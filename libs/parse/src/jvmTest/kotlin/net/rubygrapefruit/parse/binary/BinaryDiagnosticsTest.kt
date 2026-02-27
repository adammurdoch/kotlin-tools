package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.AbstractParseTest
import net.rubygrapefruit.parse.combinators.oneOrMore
import kotlin.test.Test

class BinaryDiagnosticsTest : AbstractParseTest() {
    @Test
    fun `reports failure in input`() {
        val parser = oneOrMore(literal(byteArrayOf(0x1)), separator = literal(byteArrayOf(0x2)))

        parser.doesNotMatch(0x1, 0x3, 0x1, 0) {
            failAt(1)
            expectLiteral(0x2)
            expectEndOfInput()
            expectContext("x03")
        }
    }

    @Test
    fun `reports failure at end of input`() {
        val parser = oneOrMore(literal(byteArrayOf(0x1)), separator = literal(byteArrayOf(0x2)))

        parser.doesNotMatch(0x1, 0x2) {
            failAt(2)
            expectLiteral(0x1)
            expectContext("end of input")
        }
    }
}