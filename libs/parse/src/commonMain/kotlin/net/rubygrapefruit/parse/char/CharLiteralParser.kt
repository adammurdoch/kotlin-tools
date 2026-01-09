package net.rubygrapefruit.parse.char

import net.rubygrapefruit.parse.ParseContinuation
import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.ParserBuilder
import net.rubygrapefruit.parse.PullParser

internal class CharLiteralParser<OUT>(private val text: String, private val result: OUT) : Parser<CharInput, OUT>, ParserBuilder<CharStream, OUT> {
    override fun <NEXT> build(next: ParseContinuation<CharStream, OUT, NEXT>): PullParser<CharStream, NEXT> {
        return CharLiteralPullParser(text, result, next)
    }

    private class CharLiteralPullParser<OUT, NEXT>(
        private val text: String,
        private val result: OUT,
        private val next: ParseContinuation<CharStream, OUT, NEXT>
    ) : PullParser<CharStream, NEXT> {
        private var matched = 0

        override fun toString(): String {
            return "{literal \"$text\"}"
        }

        override fun parse(input: CharStream, max: Int): PullParser.Result<CharStream, NEXT> {
            var index = 0
            val remaining = text.length - matched
            while (index < remaining) {
                if (index >= max) {
                    return if (index >= input.available && input.finished) {
                        PullParser.Failed(-matched, listOf("\"$text\""))
                    } else {
                        matched += index
                        PullParser.RequireMore(index, this)
                    }
                }
                if (input.get(index) != text[matched + index]) {
                    return PullParser.Failed(-matched, listOf("\"$text\""))
                }
                index++
            }
            return next.matched(index, result)
        }
    }
}