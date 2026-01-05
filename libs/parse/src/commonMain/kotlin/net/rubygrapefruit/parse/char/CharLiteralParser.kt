package net.rubygrapefruit.parse.char

import net.rubygrapefruit.parse.ConsumingParser
import net.rubygrapefruit.parse.Parser

internal class CharLiteralParser<OUT>(private val text: String, result: OUT) : Parser<CharInput, OUT>, ConsumingParser<CharStream, OUT> {
    private val fail = ConsumingParser.Result.Fail(0)
    private val success = ConsumingParser.Result.Success(text.length, result)

    override fun parse(input: CharStream): ConsumingParser.Result<OUT> {
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