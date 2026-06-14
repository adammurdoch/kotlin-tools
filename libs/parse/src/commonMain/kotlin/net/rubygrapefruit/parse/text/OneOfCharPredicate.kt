package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.InputPredicate

internal class OneOfCharPredicate(private val chars: CharArray) : InputPredicate<CharStream> {
    override fun toString(): String {
        return "{one-of ${chars.joinToString { format(it) }}}"
    }

    override fun match(input: CharStream, index: Int): Boolean {
        return chars.contains(input.get(index))
    }
}