Parser
=====

- Parses text or binary input
- Push parsing

- literal(chars)
  - literal(chars, result)
- literal(bytes)
  - literal(bytes, result)
- succeed()
  - succeed(result)

Combinators
- oneOf(parsers)
- sequence(parser, parser, map)
- zeroOrMore(parser)
