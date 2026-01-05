package net.rubygrapefruit.parse.char

import net.rubygrapefruit.parse.Input

internal interface CharStream : Input {
    fun next(index: Int): Char
}