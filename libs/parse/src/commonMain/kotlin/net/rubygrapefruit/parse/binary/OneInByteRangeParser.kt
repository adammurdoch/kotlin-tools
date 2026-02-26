package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.Expectation
import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.SingleInputParser

internal class OneInByteRangeParser(private val from: Byte, private val to: Byte) : Parser<BinaryInput, Byte>, SingleInputParser<ByteStream> {
    override val expectation = Expectation.One("${format(from)}..${format(to)}")

    override fun match(input: ByteStream, index: Int): Boolean {
        val value = input.get(index)
        return value >= from && value <= to
    }
}