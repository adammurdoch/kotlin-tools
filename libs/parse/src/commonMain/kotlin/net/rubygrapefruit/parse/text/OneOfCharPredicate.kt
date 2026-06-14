package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.Expectation
import net.rubygrapefruit.parse.InputPredicate

internal class OneOfCharPredicate private constructor(private val chars: CharArray) : InputPredicate<CharStream> {
    override val expectation = Expectation.oneOf(chars.map { Expectation.One(format(it)) })

    override fun toString(): String {
        return "{one-of ${chars.joinToString { format(it) }}}"
    }

    override fun match(input: CharStream, index: Int): Boolean {
        return chars.contains(input.get(index))
    }

    companion object {
        fun of(chars: Iterable<Char>): OneOfCharPredicate {
            val effective = chars.distinct()
            if (effective.size < 2) {
                throw IllegalArgumentException("2 or more characters required.")
            }
            return OneOfCharPredicate(effective.toCharArray())
        }
    }
}