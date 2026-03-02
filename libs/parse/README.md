# Parser

A Kotlin multi-platform library for parsing text and binary files and streams.

## Features

- Parse text or binary inputs
- Push parsing, allows the content of a stream to be parsed as it becomes available
- Provides a library of parsers that can be composed to produce higher level parsers
- Good quality diagnostics
- Good performance
- Targets JVM (Java 17+), macOS, Linux, Windows and JavaScript on the browser

The implementation is a bottom-up parser, meaning that grammars can be deeply nested without exhausting the stack.

## Usage

1. Create a parser using the functions listed below.
2. Use one of the parsing functions below.

## Parsing

- `parser.parse(string)` to parse a string
- `parser.parse(byte-array)` to parse a byte array
- `parser.parse(file, charset)` to parse a text file
- `parser.parse(file)` to parse a binary file
- `parser.pushParser()` to create a push parser
    - Call `pushParser.input(array)` to add more input
    - Call `pushParser.endOfInput()` to finish parsing

## Text parsers

Parsers that operate on text input:

- literal(string) - produces nothing
    - literal(string, result) - produces result
- one() - produces matched input char
- oneOf(chars) - produces matched input char
    - oneOf(char...)
    - oneOf(collection)
- oneInRange(charRange) - produces matched input char

Text combinators:

- oneExcept(parser) - produces matched input char
- match(parser) - produces matched input as a string

## Binary parsers

Parsers that operate on binary input:

- literal(bytes) - produces nothing
    - literal(bytes, result) - produces result
- one() - produces matched input byte
- oneOf(bytes) - produces matched input byte
    - oneOf(byte...)
    - oneOf(collection)
- oneInRange(byteRange) - produces matched input byte

Binary combinators:

- oneExcept(parser) - produces matched input char
- match(parser) - produces matched input as a byte array

## General parsers

Parsers that operate on any kind of input:

- succeed() - matches zero input values, produces nothing
    - succeed(result) - produces result
- endOfInput() - matches end of input, produces nothing
    - endOfInput(result) - produces result

## Combinators

Parsers that combine other parsers:

Choice parsers:

- oneOf(parsers) - produces result of first parser that matches

Sequence parsers:

- sequence(parser, parser, map) - produces result of map function
- sequence(parser, parser, parser, map) - produces result of map function
- sequence(parser, parser, parser, parser, map) - produces result of map function
- prefixed(parser, parser) - produces result of the second parser
    - sequence(unitParser, parser) - an alias
- suffixed(parser, parser) - produces result of the first parser
    - sequence(parser, unitParser) - an alias
- quoted(parser, parser, parser) - produces result of the middle parser
    - sequence(unitParser, parser, unitParser) - an alias
- separated(parser, parser, parser, map) - produces result of map function applied to result of first and last parser
    - sequence(parser, unitParser, parser, map) - an alias
- sequence(unitParser, unitParser) - produces no result
- sequence(unitParser, unitParser, unitParser) - produces no result
- decide(parser, factory) - uses factory to create second parser from the result of the first parser

Repeating parsers:

- optional(parser) - matches zero or one, produces null when missing
    - optional(parser, result) - produces given default value when missing
    - optional(unitParser) - produces nothing
- zeroOrMore(parser) - matches zero or more, produces a list
    - Stops if the parser matches zero input values
    - zeroOrMore(unitParser) - produces nothing
- zeroOrMore(parser, parser) - matches zero or more of first parser, separated by second parser, produces a list
    - zeroOrMore(unitParser, parser) - produces nothing
- oneOrMore(parser) - matches one or more, produces a list
    - Stops if the parser matches zero input values
    - oneOrMore(unitParser) - produces nothing
- oneOrMore(parser, parser) - matches one or more of first parser, separated by second parser, produces a list
    - oneOrMore(unitParser, parser) - produces nothing
- repeat(count, parser) - produces a list 
  - repeat(unitParser, parser) - produces nothing

Mapping parsers:

- map(parser, map) - produces result of map function
- discard(parser) - discards result of parser, produces nothing
- replace(parser, result) - replaces result of parser with fixed value

Other parsers:

- describedAs(parser, description) - uses the description in error messages
- recursive() - a parser that is applied recursively
- not(parser) - matches zero input values, produces nothing
- consume(parser, action) - calls function with result, then discards result
