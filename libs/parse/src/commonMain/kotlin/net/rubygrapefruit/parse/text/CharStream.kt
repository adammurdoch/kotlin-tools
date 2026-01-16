package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.Input

internal interface CharStream : Input<CharPosition> {
    fun get(index: Int): Char

    fun get(start: Int, end: Int): String
}