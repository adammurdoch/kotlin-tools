package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.ParseResult
import net.rubygrapefruit.parse.Parser
import java.io.File

/**
 * Attempts to parse the given file as binary input. Fails when the parser cannot match the entire input.
 */
fun <OUT> Parser<BinaryInput, OUT>.parse(file: File): ParseResult<BinaryFailureContext, OUT> {
    return file.inputStream().use { inputStream ->
        val parser = pushParser()
        val buffer = ByteArray(16 * 1024)
        while (true) {
            val nread = inputStream.read(buffer)
            if (nread < 0) {
                break
            }
            val failure = parser.input(buffer, 0, nread)
            if (failure != null) {
                return failure
            }
        }
        parser.endOfInput()
    }
}