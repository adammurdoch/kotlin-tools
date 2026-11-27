package net.rubygrapefruit.toml

import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.combinators.*
import net.rubygrapefruit.parse.general.endOfInput
import net.rubygrapefruit.parse.general.position
import net.rubygrapefruit.parse.text.*

class TomlParser {
    val parser: Parser<TextInput, TomlTable>

    init {
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
        val key = sequence(position(), bareKeys) { position, path -> Path(position, path) }

        val quote = literal('"')
        val escape = literal("\\")
        // not complete
        val escapes = prefixed(
            escape,
            oneOf(
                literal("\"", "\""),
                literal("\\", "\\")
            )
        )
        val basicStringSpan = oneOf(
            escapes,
            match(oneOrMore(oneExcept(oneOf(escape, quote, endLine))))
        )
        val basicStringBody = map(zeroOrMore(basicStringSpan)) { TomlString(it.joinToString("")) }
        val basicString = surrounded(quote, basicStringBody, quote)

        val singleQuote = literal("'")
        val literalStringBody = map(match(zeroOrMore(oneExcept(oneOf(singleQuote, endLine))))) { TomlString(it) }
        val literalString = surrounded(singleQuote, literalStringBody, singleQuote)

        val number = map(integer()) { TomlInt(it) }

        val boolean = oneOf(literal("true", TomlBoolean(true)), literal("false", TomlBoolean(false)))

        val value = recursive<TextInput, TomlValue>()

        val arrayWhitespace = sequence(blankLines, optionalWhitespace)
        val arrayPrefix = sequence(literal("["), arrayWhitespace)
        val arraySeparator = sequence(arrayWhitespace, literal(","), arrayWhitespace)
        val arrayItem = suffixed(value, arraySeparator)
        val arrayLastItem = suffixed(value, arrayWhitespace)
        // allow optional trailing ','
        val arrayItems = sequence(zeroOrMore(arrayItem), optional(arrayLastItem)) { a, b -> if (b == null) a else a + b }
        val arraySuffix = literal("]")
        val array = surrounded(arrayPrefix, arrayItems, arraySuffix) { TomlArray(it) }

        value.parser(oneOf(basicString, literalString, number, boolean, array))

        val equals = sequence(optionalWhitespace, literal("="), optionalWhitespace)
        val pair = separated(key, equals, value) { key, value -> KeyValuePairTree(key, value) }
        val pairLine = surrounded(optionalWhitespace, pair, blankLine)
        val pairs = prefixed(blankLines, zeroOrMore(suffixed(pairLine, blankLines)))

        val tablePath = surrounded(literal("["), key, literal("]"))
        val tableHeader = suffixed(tablePath, blankLine)
        val table = prefixed(blankLines, tableHeader, pairs) { header, pairs -> TableTree(header, pairs) }

        val tableArrayPath = surrounded(literal("[["), key, literal("]]"))
        val tableArrayHeader = suffixed(tableArrayPath, blankLine)
        val tableArray = prefixed(blankLines, tableArrayHeader, pairs) { header, pairs -> TableArrayTree(header, pairs) }

        val section = oneOf(table, tableArray)

        val file = sequence(pairs, zeroOrMore(section)) { a, b -> FileTree(a, b) }
        parser = map(file) { it.resolve() }
    }

    fun parse(text: String): TomlTable {
        return parser.parse(text).get()
    }
}