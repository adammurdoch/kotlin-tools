package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.Expectation
import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.SingleInputParser

internal class OneInCharRangeParser(val chars: CharRange) : Parser<TextInput, Char>, SingleInputParser<CharStream> {
    override val expectation = Expectation.One("${format(chars.first)}..${format(chars.last)}")

    override fun toString(): String {
        return "{one-in $chars}"
    }

    override fun match(input: CharStream, index: Int): Boolean {
        return chars.contains(input.get(index))
    }
}