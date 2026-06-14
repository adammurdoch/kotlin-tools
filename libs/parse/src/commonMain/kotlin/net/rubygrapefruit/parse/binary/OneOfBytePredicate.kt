package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.InputPredicate

internal class OneOfBytePredicate(private val bytes: ByteArray) : InputPredicate<ByteStream> {
    override fun toString(): String {
        return "{one-of ${bytes.joinToString { format(it) }}}"
    }

    override fun match(input: ByteStream, index: Int): Boolean {
        return bytes.contains(input.get(index))
    }
}