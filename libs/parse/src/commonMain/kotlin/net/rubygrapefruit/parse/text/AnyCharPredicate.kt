package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.InputPredicate

internal object AnyCharPredicate : InputPredicate<CharStream> {
    override fun toString(): String {
        return "{any-char}"
    }

    override fun match(input: CharStream, index: Int): Boolean {
        return true
    }
}