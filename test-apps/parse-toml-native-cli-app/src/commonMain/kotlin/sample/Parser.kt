package sample

import net.rubygrapefruit.file.RegularFile
import net.rubygrapefruit.parse.combinators.*
import net.rubygrapefruit.parse.text.literal
import net.rubygrapefruit.parse.text.match
import net.rubygrapefruit.parse.text.oneOf
import net.rubygrapefruit.parse.text.parse

class Parser {
    fun parse(file: RegularFile): Root {
        val whitespace = oneOf(' ', '\t')
        val endLine = oneOf(literal("\n"), literal("\r\n"))

        val keyChar = oneOf(
            oneOf('a'..'z'),
            oneOf('A'..'Z'),
            oneOf('0'..'9'),
            oneOf('_', '-')
        )
        val key = match(sequence(discard(keyChar), zeroOrMore(discard(keyChar))))

        val quote = literal("\"")
        val stringChar = oneOf('a'..'z') // not right
        val stringBody = match(zeroOrMore(sequence(not(oneOf(quote, endLine)), stringChar)))
        val string = sequence(quote, stringBody, quote)

        val equals = literal("=")
        val pair = sequence(key, equals, string) { key, _, value -> Leaf(key, value) }
        val line = sequence(pair, endLine) { pair, _ -> pair }
        val lines = zeroOrMore(line)

        val leaves = lines.parse(file.readText())

        return Root(leaves.get())
    }
}