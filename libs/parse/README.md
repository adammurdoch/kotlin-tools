Parser
=====

- Parses text or binary input
- Push parsing

Text parsers
- literal(chars)
  - literal(chars, result)
- oneOf(chars)

Binary parsers
- literal(bytes)
  - literal(bytes, result)
- oneOf(bytes)

General parsers
- succeed()
  - succeed(result)

Combinators
- oneOf(parsers)
- sequence(parser, parser, map)
- zeroOrMore(parser)
