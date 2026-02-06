# Parser

A parser combinator library for Kotlin multi-platform.

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

Parsers that operate on text input:

- literal(chars) - produces nothing
  - literal(chars, result) - produces result
- one() - produces matched input char
- oneOf(chars) - produces matched input char
  - oneOf(char...)
  - oneOf(charRange)
  - oneOf(collection)

Text combinators:

- match(parser) - produces matched input

## Binary parsers

Parsers that operate on binary input:

- literal(bytes) - produces nothing
  - literal(bytes, result) - produces result
- one() - produces matched input byte
- oneOf(bytes) - produces matched input byte
  - oneOf(byte...)
  - oneOf(byteRange)
  - oneOf(collection)

Binary combinators:

- match(parser) - produces matched input

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

- zeroOrMore(parser) - produces list
  - Stops if the parser matches zero input values
  - zeroOrMore(unitParser) - produces nothing

Mapping parsers:

- map(parser, map) - produces result of map function
- discard(parser) - discards result of parser, produces nothing
- replace(parser, result) - replaces result of parser with fixed value

Other parsers:

- recursive() - a parser that is applied recursively
- not(parser) - matches zero input values, produces nothing
- consume(parser, action) - calls function with result, then discards result
