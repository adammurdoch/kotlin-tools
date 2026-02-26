package net.rubygrapefruit.parse.file

import net.rubygrapefruit.parse.ParseResult
import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.text.CharFailureContext
import net.rubygrapefruit.parse.text.CharInput
import net.rubygrapefruit.parse.text.pushParser
import java.io.File
import java.nio.charset.Charset

/**
 * Attempts to parse the given file as text. Fails when the parser cannot match the entire input.
 */
fun <OUT> Parser<CharInput, OUT>.parse(file: File, charset: Charset): ParseResult<CharFailureContext, OUT> {
    return file.bufferedReader(charset).use { reader ->
        val parser = pushParser()
        val buffer = CharArray(1024)
        while (true) {
            val nread = reader.read(buffer)
            if (nread < 0) {
                break
            }
            parser.input(buffer, 0, nread)
        }
        parser.endOfInput()
    }
}