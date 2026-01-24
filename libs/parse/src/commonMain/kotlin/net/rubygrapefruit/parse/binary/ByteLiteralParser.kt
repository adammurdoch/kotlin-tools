package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.*

internal class ByteLiteralParser<OUT>(private val bytes: ByteArray, val result: OUT) : Parser<ByteInput, OUT>, ParserBuilder<ByteStream, OUT> {
    private val expectations = bytes.map { Expectation.One(format(it)) }

    override val expectation: Expectation
        get() = expectations.first()

    override fun withNoResult(): ParserBuilder<ByteStream, Unit> {
        return ByteLiteralParser(bytes, Unit)
    }

    override fun <NEXT> start(next: ParseContinuation<ByteStream, OUT, NEXT>): PullParser<ByteStream, NEXT> {
        return ByteLiteralPullParser(bytes, result, expectations, next)
    }

    private class ByteLiteralPullParser<OUT, NEXT>(
        private val bytes: ByteArray,
        private val result: OUT,
        private val expectations: List<Expectation>,
        private val next: ParseContinuation<ByteStream, OUT, NEXT>
    ) : PullParser<ByteStream, NEXT> {
        private var matched = 0

        override val expectation: Expectation
            get() = expectations[matched]

        override fun toString(): String {
            return "{literal ${bytes.map { format(it) }}}"
        }

        override fun parse(input: ByteStream, max: Int): PullParser.Result<ByteStream, NEXT> {
            var index = 0
            val remaining = bytes.size - matched
            while (index < remaining) {
                if (index >= max) {
                    return if (index >= input.available && input.finished) {
                        PullParser.Failed(index, expectations[matched + index])
                    } else {
                        matched += index
                        PullParser.RequireMore(index, this)
                    }
                }
                if (input.get(index) != bytes[matched + index]) {
                    return PullParser.Failed(index, expectations[matched + index])
                }
                index++
            }
            return next.matched(-matched, index, result)
        }
    }
}