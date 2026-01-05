package net.rubygrapefruit.parse.char

import net.rubygrapefruit.parse.Input

internal interface CharInput : Input {
    fun next(index: Int): Char
}