package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.Expectation
import net.rubygrapefruit.parse.InputPredicate

internal object OneBytePredicate : InputPredicate<ByteStream> {
    override val expectation: Expectation = Expectation.One("any byte")

    override fun toString(): String {
        return "{any-byte}"
    }

    override fun match(input: ByteStream, index: Int): Boolean {
        return true
    }
}