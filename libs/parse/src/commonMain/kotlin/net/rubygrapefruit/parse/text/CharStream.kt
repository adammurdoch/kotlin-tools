package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.BoxingInput
import net.rubygrapefruit.parse.Input
import net.rubygrapefruit.parse.SlicingInput

internal interface CharStream : Input<CharPosition>, SlicingInput<String>, BoxingInput<CharPosition, Char> {
    fun get(index: Int): Char

    override fun getBoxed(index: Int): Char {
        return get(index)
    }
}