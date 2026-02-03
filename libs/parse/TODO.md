Parsers
- "literal" -> "text" and "bytes"?
- Improve description for ranges
- `description()`
- `separated()`
- `oneOrMore()`
- `zeroOrMore()` and `oneOrMore()` with a separator
- Binary ints
- Repeat, `take(parser<int>)`, `drop(parser<int>)`
- `accept()` does not call function on a failed branch
- Improve expected message for not e.g: expecting one character but not "  

Performance
- Optimized implementation for `not(single-input)`
- `literal(one-value)` is a single input parser
- `oneOf(single-input...)` is a single input parser
- Allow `CompiledParser.start()` to fail or complete parsing
- Choice exits when there is one remaining candidate
- Discard buffered input once it is not required
- Match common prefix for choice

Features
- Parse file
- Match location/region
- Semantic errors
- Sync on parse failure
- Left recursion