package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.Expectation
import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.SingleInputParser

internal class OneOfCharRangeParser(val chars: CharRange) : Parser<CharInput, Char>, SingleInputParser<CharStream> {
    override val expectation = Expectation.One("${format(chars.first)}..${format(chars.last)}")

    override fun match(input: CharStream, index: Int): Boolean {
        return chars.contains(input.get(index))
    }
}