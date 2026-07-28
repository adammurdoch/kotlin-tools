package net.ubygrapefruit.parse.text

import net.rubygrapefruit.file.RegularFile
import net.rubygrapefruit.parse.ParseResult
import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.text.TextFailureContext
import net.rubygrapefruit.parse.text.TextInput
import net.rubygrapefruit.parse.text.pushParser

/**
 * Attempts to parse the given file. Fails when the parser cannot match the entire input.
 */
fun <OUT> Parser<TextInput, OUT>.parse(file: RegularFile): ParseResult<TextFailureContext, OUT> {
    return file.read { source ->
        val parser = pushParser()
        parser.takeFrom { buffer, offset, _ ->
            if (source.request(1)) {
                val byte = source.readByte().toInt()
                val ch = if (byte.and(0x80) == 0) {
                    Char(byte)
                } else if (byte.and(0xE0) == 0xC0) {
                    val byte2 = source.readByte().toInt()
                    Char(
                        byte.and(0x1F).rotateLeft(6)
                            .or(byte2.and(0x3F))
                    )
                } else if (byte.and(0xF0) == 0xE0) {
                    val byte2 = source.readByte().toInt()
                    val byte3 = source.readByte().toInt()
                    Char(
                        byte.and(0xF).rotateLeft(12)
                            .or(byte2.and(0x3F).rotateLeft(6))
                            .or(byte3.and(0x3F))
                    )
                } else {
                    TODO()
                }
                buffer[offset] = ch
                1
            } else {
                -1
            }
        }
        parser.endOfInput()
    }
}
