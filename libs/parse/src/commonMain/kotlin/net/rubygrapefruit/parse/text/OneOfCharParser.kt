package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.Expectation
import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.SingleInputParser

internal class OneOfCharParser(val chars: CharArray) : Parser<CharInput, Char>, SingleInputParser<CharStream, Char> {
    override val expectation = Expectation.OneOf(chars.map { Expectation.One(format(it)) })

    override fun toString(): String {
        return "{one-of ${chars.joinToString { format(it) }}}"
    }

    override fun match(input: CharStream, index: Int): Boolean {
        return chars.contains(input.get(index))
    }
}