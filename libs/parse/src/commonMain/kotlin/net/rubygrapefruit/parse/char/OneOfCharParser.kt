package net.rubygrapefruit.parse.char

import net.rubygrapefruit.parse.ParseContinuation
import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.ParserBuilder
import net.rubygrapefruit.parse.PullParser

internal class OneOfCharParser(private val chars: CharArray) : Parser<CharInput, Char>, ParserBuilder<CharStream, Char> {
    override fun <NEXT> build(next: ParseContinuation<CharStream, Char, NEXT>): PullParser<CharStream, NEXT> {
        return OneOfCharPullParser(chars, next)
    }

    private class OneOfCharPullParser<NEXT>(private val chars: CharArray, private val next: ParseContinuation<CharStream, Char, NEXT>) : PullParser<CharStream, NEXT> {
        override fun parse(input: CharStream, max: Int): PullParser.Result<CharStream, NEXT> {
            return if (max == 0) {
                if (input.finished) {
                    PullParser.Failed(0, chars.map { format(it) })
                } else {
                    PullParser.RequireMore(0, this)
                }
            } else {
                val ch = input.get(0)
                if (chars.contains(ch)) {
                    next.matched(1, ch)
                } else {
                    PullParser.Failed(0, chars.map { format(it) })
                }
            }
        }
    }
}