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
            discard(zeroOrMore(oneExcept(endLine))),
        )
        val blankLine = sequence(
            optionalWhitespace,
            optional(lineComment),
            endLine
        )
        val blankLines = zeroOrMore(blankLine)

        val keyChar = describedAs(
            oneOf(
                oneInRange('a'..'z'),
                oneInRange('A'..'Z'),
                oneInRange('0'..'9'),
                oneOf('_', '-')
            ), "a key character"
        )
        val bareKey = match(oneOrMore(keyChar))
        val bareKeys = oneOrMore(bareKey, separator = literal("."))
        val key = map(bareKeys) { Path(it) }

        val quote = literal("\"")
        val escape = literal("\\")
        // not complete
        val escapes = sequence(
            escape, oneOf(
                literal("\"", "\""),
                literal("\\", "\\")
            )
        )
        val basicStringSpan = oneOf(
            escapes,
            match(oneOrMore(oneExcept(oneOf(escape, quote, endLine))))
        )
        val basicStringBody = map(zeroOrMore(basicStringSpan)) { it.joinToString("") }
        val basicString = sequence(quote, basicStringBody, quote)

        val singleQuote = literal("'")
        val literalStringBody = match(zeroOrMore(oneExcept(oneOf(singleQuote, endLine))))
        val literalString = sequence(singleQuote, literalStringBody, singleQuote)

        val digit = describedAs(oneInRange('0'..'9'), "a digit")
        val digits = match(oneOrMore(digit))
        val number = map(digits) { it.toInt() }

        val boolean = oneOf(literal("true", true), literal("false", false))

        val value = recursive<TextInput, Any>()

        val arrayWhitespace = sequence(blankLines, optionalWhitespace)
        val arrayPrefix = sequence(literal("["), arrayWhitespace)
        val arraySeparator = sequence(arrayWhitespace, literal(","), arrayWhitespace)
        val arrayItem = sequence(value, arraySeparator)
        val arrayLastItem = sequence(value, arrayWhitespace)
        // allow optional trailing ','
        val arrayItems = sequence(zeroOrMore(arrayItem), optional(arrayLastItem)) { a, b -> if (b == null) a else a + b }
        val arraySuffix = literal("]")
        val array = sequence(arrayPrefix, arrayItems, arraySuffix)

        value.parser(oneOf(basicString, literalString, number, boolean, array))

        val equals = sequence(optionalWhitespace, literal("="), optionalWhitespace)
        val pair = sequence(key, equals, value) { key, value -> ValueTree(key, value) }
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