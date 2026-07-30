package net.rubygrapefruit.parse.binary.file

import net.rubygrapefruit.file.RegularFile
import net.rubygrapefruit.parse.ParseResult
import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.binary.BinaryFailureContext
import net.rubygrapefruit.parse.binary.BinaryInput
import net.rubygrapefruit.parse.binary.pushParser

/**
 * Attempts to parse the given file. Fails when the parser cannot match the entire input.
 */
fun <OUT> Parser<BinaryInput, OUT>.parse(file: RegularFile): ParseResult<BinaryFailureContext, OUT> {
    return file.read { source ->
        val parser = pushParser()
        parser.takeFrom { buffer, offset, max ->
            source.readAtMostTo(buffer, offset, offset + max)
        }
        parser.endOfInput()
    }
}