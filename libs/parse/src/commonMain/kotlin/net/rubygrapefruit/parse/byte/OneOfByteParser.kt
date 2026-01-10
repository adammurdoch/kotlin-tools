package net.rubygrapefruit.parse.byte

import net.rubygrapefruit.parse.ParseContinuation
import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.ParserBuilder
import net.rubygrapefruit.parse.PullParser

internal class OneOfByteParser(private val bytes: ByteArray) : Parser<ByteInput, Byte>, ParserBuilder<ByteStream, Byte> {
    override fun <NEXT> build(next: ParseContinuation<ByteStream, Byte, NEXT>): PullParser<ByteStream, NEXT> {
        return OneOfBytePullParser(bytes, next)
    }

    private class OneOfBytePullParser<NEXT>(private val bytes: ByteArray, private val next: ParseContinuation<ByteStream, Byte, NEXT>) : PullParser<ByteStream, NEXT> {
        override fun parse(input: ByteStream, max: Int): PullParser.Result<ByteStream, NEXT> {
            return if (max == 0) {
                if (input.finished) {
                    PullParser.Failed(0, bytes.map { format(it) })
                } else {
                    PullParser.RequireMore(0, this)
                }
            } else {
                val byte = input.get(0)
                if (bytes.contains(byte)) {
                    next.matched(1, byte)
                } else {
                    PullParser.Failed(0, bytes.map { format(it) })
                }
            }
        }
    }
}