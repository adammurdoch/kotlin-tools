Parsers
- "literal" -> "text" and "bytes"?
- `repeat(parser, parser)` where first parser produces the count
- `repeat()` with a separator
- `concat(parser<string>)`
- Binary ints
- `take(n)`, `drop(n)`, `take(parser<int>)`, `drop(parser<int>)` to match n input values
- `oneExcept(parser...)` convenience
- `oneOrMore()` and `zeroOrMore()` allow optional trailing separator
- `upTo(parser)`
- `accept()` does not call function on a failed branch
- Improve expected message for `not()` e.g: expecting any character but not "x"  

Performance
- Optimized implementation for `not(single-input)`
- Optimized implementation for `optional(single-input)`
- Optimized implementation for `oneOrMore(single-input)`
- Optimized implementation for `repeat(single-input)`
- `literal(one-value)` is a single input parser
- `oneOf(single-input...)` is a single input parser
- `sequence(not(single-input), single-input)` is a single input parser
- `describedAs(single-input)` is a single input parser
- `oneExcept(single-input)` is a single input parser
- Allow `CompiledParser.start()` to fail or complete parsing
- Choice exits when there is one remaining candidate that has not failed
  - Need to know that candidate will not fail before/at other failed options
- Choice continuation is deeply nested 
- Discard buffered input once it is not required
- Choice matches common prefix of options
- Read from file/stream in parallel with parsing

Features
- Parse input stream
- Parse kotlinx-io Source
- Parser file-io files
- Match location/region
- Semantic errors
- Android, iOS and wasm targets
- Sync on parse failure
- Left recursion