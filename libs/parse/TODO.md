Parsers
- "literal" -> "text" and "bytes"?
- `literal(char)` and `literal(byte)` convenience
- `ParseResult` and `MappingResult` use consistent names, e.g. "success" and "failure" or "succeeded" and "failed"
- Case-insensitive text literal
- `repeat(parser, parser)` where first parser produces the count
- `concat(parser<list<string>>)` - collect result of parser into a string
- 32 bit binary ints
- `take(n)`, `drop(n)`, `take(parser<int>)`, `drop(parser<int>)` to match n input values
- `oneExcept(parser...)` convenience
- `oneOrMore()`, `zeroOrMore()` and `repeat()` allow optional trailing separator
- `upTo(parser)`
- `oneOf(pair<String, OUT>...)` convenience
- `accept()` should not call function on a failed branch
- `discard(parser<unit>...)` - alias for `sequence(parser<unit>...)`?
- `integer(parser)` or `integer(minLen, maxLen)` - converts matching input to int
- Improve expected message for `not()` or perhaps `oneExcept()`
- Line comment convenience
- Quoted string convenience

Performance
- Optimized implementation for `not(single-input)`
- Optimized implementation for `optional(single-input)`
- Optimized implementation for `oneOrMore(single-input)`
- Optimized implementation for `repeat(single-input)`
- Optimized implementation for `literal(one-value)`
  - `discard(literal(one-value))` is a single input parser
- `oneOf(single-input...)` is a single input parser
- `digit()` is a single input parser
- `sequence(not(single-input), single-input)` is a single input parser
- `describedAs(single-input)` is a single input parser
- `oneExcept(single-input)` is a single input parser
- More efficient implementation of 3+ sequences
- Allow `CompiledParser.start()` to fail or complete parsing
  - This requires the parser to look ahead
  - Passing input stream to `start()` means parse continuation will need the steam in the correct state to start next parser
- Binary int parsers should not box values
- Discard buffered input once it is not required
- Choice matches common prefix of options
- Read from file/stream in parallel with parsing

Features
- Parse kotlinx-io Source
- Parse file-io files
- Match location/region
- Semantic errors
- Android, iOS and wasm targets
- Sync and continue on parse failure
- Left recursion