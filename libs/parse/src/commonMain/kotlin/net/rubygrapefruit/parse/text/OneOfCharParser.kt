package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.Expectation
import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.SingleInputParser

internal class OneOfCharParser private constructor(val chars: List<Char>) : Parser<CharInput, Char>, SingleInputParser<CharStream> {
    override val expectation = Expectation.OneOf(chars.map { Expectation.One(format(it)) })

    override fun toString(): String {
        return "{one-of ${chars.joinToString { format(it) }}}"
    }

    override fun match(input: CharStream, index: Int): Boolean {
        return chars.contains(input.get(index))
    }

    companion object {
        fun of(chars: Iterable<Char>): OneOfCharParser {
            val effective = chars.distinct()
            if (effective.size < 2) {
                throw IllegalArgumentException("2 or more characters required.")
            }
            return OneOfCharParser(effective)
        }
    }
}