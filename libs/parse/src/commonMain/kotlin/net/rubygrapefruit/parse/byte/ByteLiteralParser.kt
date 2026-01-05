package net.rubygrapefruit.parse.byte

import net.rubygrapefruit.parse.ConsumingParser
import net.rubygrapefruit.parse.Parser

internal class ByteLiteralParser<OUT>(private val bytes: ByteArray, result: OUT) : Parser<ByteInput, OUT>, ConsumingParser<ByteStream, OUT> {
    private val fail = ConsumingParser.Result.Fail(0)
    private val success = ConsumingParser.Result.Success(bytes.size, result)

    override fun parse(input: ByteStream): ConsumingParser.Result<OUT> {
        if (input.length < bytes.size) {
            return fail
        }
        for (index in bytes.indices) {
            if (input.next(index) != bytes[index]) {
                return fail
            }
        }
        return success
    }
}