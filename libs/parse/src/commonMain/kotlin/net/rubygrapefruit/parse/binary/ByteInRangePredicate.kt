package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.InputPredicate

internal class ByteInRangePredicate(private val from: Byte, private val to: Byte) : InputPredicate<ByteStream> {
    override fun toString(): String {
        return "{one-in ${format(from)}..${format(to)}}"
    }

    override fun match(input: ByteStream, index: Int): Boolean {
        val value = input.get(index)
        return value >= from && value <= to
    }
}