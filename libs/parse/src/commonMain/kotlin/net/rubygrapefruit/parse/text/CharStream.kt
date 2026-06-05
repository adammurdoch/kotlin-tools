package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.stream.BoxingInput
import net.rubygrapefruit.parse.stream.Input
import net.rubygrapefruit.parse.SlicingInput

internal interface CharStream : Input<CharPosition>, SlicingInput<String>, BoxingInput<CharPosition, Char> {
    fun get(index: Int): Char

    override fun getBoxed(index: Int): Char {
        return get(index)
    }
}