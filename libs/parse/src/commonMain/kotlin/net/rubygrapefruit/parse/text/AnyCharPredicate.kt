package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.Expectation
import net.rubygrapefruit.parse.InputPredicate

internal object AnyCharPredicate : InputPredicate<CharStream> {
    override val expectation: Expectation = Expectation.One("any character")

    override fun toString(): String {
        return "{any-char}"
    }

    override fun match(input: CharStream, index: Int): Boolean {
        return true
    }
}