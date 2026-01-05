package net.rubygrapefruit.parse.char

import net.rubygrapefruit.parse.ConsumingParser
import net.rubygrapefruit.parse.Parser

internal class CharLiteralParser(private val text: String) : Parser<CharInput, Unit>, ConsumingParser<CharStream, Unit> {
    private val fail = ConsumingParser.Result.Fail(0)
    private val success = ConsumingParser.Result.Success(text.length, Unit)

    override fun parse(input: CharStream): ConsumingParser.Result<Unit> {
        if (input.length < text.length) {
            return fail
        }
        for (index in text.indices) {
            if (input.next(index) != text[index]) {
                return fail
            }
        }
        return success
    }
}