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

- literal(chars)
  - literal(chars, result)
- oneOf(chars)

Text combinators

- match(parser)

## Binary parsers

Parsers that operate on binary input

- literal(bytes)
  - literal(bytes, result)
- oneOf(bytes)

Binary combinators

- match(parser)

## General parsers

Parsers that operate on any kind of input

- succeed()
  - succeed(result)

## Combinators

Parsers that combine other parsers

- oneOf(parsers)
- sequence(parser, parser, map)
- zeroOrMore(parser)
- not(parser)
