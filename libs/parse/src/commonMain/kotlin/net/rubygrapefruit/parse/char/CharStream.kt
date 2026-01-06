package net.rubygrapefruit.parse.char

import net.rubygrapefruit.parse.Input

internal interface CharStream : Input<CharPosition> {
    fun get(index: Int): Char
}