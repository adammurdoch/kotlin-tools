package sample

import net.rubygrapefruit.file.RegularFile
import net.rubygrapefruit.parse.combinators.*
import net.rubygrapefruit.parse.general.endOfInput
import net.rubygrapefruit.parse.text.*

class Parser {
    fun parse(file: RegularFile): Table {
        val whitespace = discard(oneOf(' ', '\t'))
        val optionalWhitespace = zeroOrMore(whitespace)

        val endLine = oneOf(
            literal("\n"),
            literal("\r\n"),
            endOfInput()
        )

        val lineComment = sequence(
            literal("#"),
            discard(zeroOrMore(sequence(not(endLine), one()))),
        )
        val blankLine = sequence(
            optionalWhitespace,
            optional(lineComment),
            endLine
        )
        val blankLines = zeroOrMore(blankLine)

        val keyChar = describedAs(
            oneOf(
                oneOf('a'..'z'),
                oneOf('A'..'Z'),
                oneOf('0'..'9'),
                oneOf('_', '-')
            ), "a key character"
        )
        val bareKey = match(sequence(discard(keyChar), zeroOrMore(discard(keyChar))))
        val key = sequence(bareKey, zeroOrMore(sequence(literal("."), bareKey))) { a, b ->
            Path(listOf(a) + b)
        }

        val quote = literal("\"")
        // not complete
        val stringContent = oneOf(
            literal("\\\"", '"'),
            literal("\\\\", '\\'),
            sequence(not(oneOf(quote, endLine)), one())
        )
        val stringBody = map(zeroOrMore(stringContent)) { it.joinToString("") }
        val string = sequence(quote, stringBody, quote)

        val digit = describedAs(oneOf('0'..'9'), "a digit")
        val number = map(match(sequence(discard(digit), zeroOrMore(digit)))) { it.toInt() }

        val value = oneOf(string, number)

        val equals = sequence(optionalWhitespace, literal("="), optionalWhitespace)
        val pair = sequence(key, equals, value) { key, value -> Pair(key, value) }
        val pairLine = sequence(optionalWhitespace, pair, blankLine)
        val pairs = sequence(blankLines, zeroOrMore(sequence(pairLine, blankLines)))

        val tablePath = sequence(literal("["), key, literal("]"))
        val tableHeader = sequence(tablePath, blankLine)
        val table = sequence(blankLines, tableHeader, pairs) { _, header, pairs -> TableTree(header, pairs) }

        val tomlFile = sequence(pairs, zeroOrMore(table)) { a, b -> FileTree(a, b) }

        val leaves = tomlFile.parse(file.readText())

        return Table.of(leaves.get())
    }
}