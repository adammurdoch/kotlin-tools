package sample

import net.rubygrapefruit.file.RegularFile
import net.rubygrapefruit.parse.combinators.*
import net.rubygrapefruit.parse.text.*

class Parser {
    fun parse(file: RegularFile): Root {
        val whitespace = discard(oneOf(' ', '\t'))
        val optionalWhitespace = zeroOrMore(whitespace)

        val endLine = oneOf(literal("\n"), literal("\r\n"))

        val keyChar = oneOf(
            oneOf('a'..'z'),
            oneOf('A'..'Z'),
            oneOf('0'..'9'),
            oneOf('_', '-')
        )
        val key = match(sequence(discard(keyChar), zeroOrMore(discard(keyChar))))

        val quote = literal("\"")
        // not right
        val stringContent = oneOf(
            literal("\\\"", '"'),
            literal("\\\\", '\\'),
            sequence(not(oneOf(quote, endLine)), one())
        )
        val stringBody = map(zeroOrMore(stringContent)) { it.joinToString("") }
        val string = sequence(quote, stringBody, quote)

        val equals = sequence(optionalWhitespace, literal("="), optionalWhitespace)
        val pair = sequence(key, equals, string) { key, value -> Leaf(key, value) }
        val line = sequence(pair, endLine)
        val lines = zeroOrMore(line)

        val leaves = lines.parse(file.readText())

        return Root(leaves.get())
    }
}