package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.Input

internal interface CharStream : Input<CharPosition> {
    fun get(index: Int): Char

    /**
     * @param start inclusive
     * @param end exclusive
     */
    fun get(start: Int, end: Int): String
}