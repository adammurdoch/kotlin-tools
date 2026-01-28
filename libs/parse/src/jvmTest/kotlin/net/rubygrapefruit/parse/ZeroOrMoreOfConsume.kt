package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.literal
import net.rubygrapefruit.parse.binary.parse
import net.rubygrapefruit.parse.binary.pushParser
import net.rubygrapefruit.parse.combinators.consume
import net.rubygrapefruit.parse.combinators.zeroOrMore
import kotlin.test.Test
import kotlin.test.assertEquals

class ZeroOrMoreOfConsume : AbstractParseTest() {
    @Test
    fun `matches zero or more literals`() {
        var counter = 0

        val parser = zeroOrMore(
            consume(literal(byteArrayOf(0x1, 0x2), 1)) {
                counter++
            }
        )

        parser.parse(byteArrayOf(0x1, 0x2, 0x1, 0x2))
        assertEquals(2, counter)
    }

    @Test
    fun `calls function as input becomes available`() {
        var counter = 0

        val parser = zeroOrMore(
            consume(literal(byteArrayOf(0x1, 0x2), 1)) {
                counter++
            }
        )

        val pushParser = parser.pushParser()

        pushParser.input(byteArrayOf(0x1))
        assertEquals(0, counter)

        pushParser.input(byteArrayOf(0x2))
        assertEquals(1, counter)

        pushParser.input(byteArrayOf(0x1))
        assertEquals(1, counter)

        pushParser.input(byteArrayOf(0x2))
        assertEquals(2, counter)

        pushParser.endOfInput()
        assertEquals(2, counter)
    }
}