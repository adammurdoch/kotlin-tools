# Parser

A parser combinator library for Kotlin multi-platform

## Features

- Parses text or binary input
- Push parsing, allows the content of a stream to be parsed

## Usage

1. Create a parser using the functions listed below.
2. Use one of the parsing functions below.

## Parsing

- `parser.parse(string)` to parse a string
- `parser.parse(byte-array)` to parse a byte array
- `parser.pushParser()` to create a push parser
  - Call `pushParser.input(array)` to add more input
  - Call `pushParser.endOfInput()` to finish parsing

## Text parsers

Parsers that operate on text input

- literal(chars) - produces nothing
  - literal(chars, result) - produces result
- oneOf(chars) - produces matched char

Text combinators

- match(parser) - produces matched input

## Binary parsers

Parsers that operate on binary input

- literal(bytes) - produces nothing
  - literal(bytes, result) - produces result
- oneOf(bytes) - produces matched char

Binary combinators

- match(parser) - produces matched input

## General parsers

Parsers that operate on any kind of input

- succeed() - matches zero input values, produces nothing
  - succeed(result) - produces result

## Combinators

Parsers that combine other parsers

- oneOf(parsers) - produces result of first matching parser
- sequence(parser, parser, map) - produces result of map function
- prefixed(parser, parser) - produces result of the second parser
  - sequence(unitParser, parser) - an alias
- suffixed(parser, parser) - produces result of the first parser
  - sequence(parser, unitParser) - an alias
- zeroOrMore(parser) - produces list
  - Stops if the parser matches zero input values
  - zeroOrMore(unitParser) - produces nothing
- not(parser) - matches zero input values, produces nothing
