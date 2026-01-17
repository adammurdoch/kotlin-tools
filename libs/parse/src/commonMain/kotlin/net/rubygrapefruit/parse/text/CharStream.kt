package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.Input
import net.rubygrapefruit.parse.SlicingInput

internal interface CharStream : Input<CharPosition>, SlicingInput<String> {
    fun get(index: Int): Char
}