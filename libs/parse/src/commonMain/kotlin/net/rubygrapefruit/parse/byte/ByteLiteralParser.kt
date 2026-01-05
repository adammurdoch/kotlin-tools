package net.rubygrapefruit.parse.byte

import net.rubygrapefruit.parse.ByteStream
import net.rubygrapefruit.parse.ConsumingParser
import net.rubygrapefruit.parse.Parser

internal class ByteLiteralParser(val bytes: ByteArray) : Parser<ByteStream, Unit>, ConsumingParser<ByteInput, Unit> {
    private val fail = ConsumingParser.Result.Fail(0)
    private val success = ConsumingParser.Result.Success(bytes.size, Unit)

    override fun parse(input: ByteInput): ConsumingParser.Result<Unit> {
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