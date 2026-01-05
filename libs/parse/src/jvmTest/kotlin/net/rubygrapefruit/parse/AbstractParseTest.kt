package net.rubygrapefruit.parse

abstract class AbstractParseTest {
    fun matches(parser: Parser<CharStream, Unit>, input: String, config: CharParseFixture.() -> Unit = {}) {
        TODO()
    }

    fun doesNotMatch(parser: Parser<CharStream, Unit>, input: String, config: CharParseFixture.() -> Unit = {}) {
        TODO()
    }

    fun matches(parser: Parser<ByteStream, Unit>, vararg input: Byte, config: ByteParseFixture.() -> Unit = {}) {
        TODO()
    }

    fun doesNotMatch(parser: Parser<ByteStream, Unit>, vararg input: Byte, config: ByteParseFixture.() -> Unit = {}) {
        TODO()
    }

    interface CharParseFixture

    interface ByteParseFixture
}