package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.Expectation
import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.SingleInputParser

internal object OneCharParser : Parser<CharInput, Char>, SingleInputParser<CharStream> {
    override val expectation: Expectation = Expectation.One("one character")

    override fun toString(): String {
        return "{one-char}"
    }

    override fun match(input: CharStream, index: Int): Boolean {
        return true
    }
}