package net.rubygrapefruit.parse.char

import net.rubygrapefruit.parse.Input

internal interface CharStream : Input {
    fun get(index: Int): Char
}