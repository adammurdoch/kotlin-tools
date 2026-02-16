Parsers
- "literal" -> "text" and "bytes"?
- Improve description for ranges
- `oneOrMore()`
- `zeroOrMore()` and `oneOrMore()` with a separator
- `concat(parser<char>)`
- Binary ints
- Repeat, `take(parser<int>)`, `drop(parser<int>)`
- `accept()` does not call function on a failed branch
- Improve expected message for `not()` e.g: expecting one character but not "  

Performance
- Optimized implementation for `not(single-input)`
- `literal(one-value)` is a single input parser
- `oneOf(single-input...)` is a single input parser
- `sequence(not(single-input), single-input)` is a single input parser
- `describedAs(single-input)` is a single input parser 
- Allow `CompiledParser.start()` to fail or complete parsing
- Choice exits when there is one remaining candidate that has not failed
  - Need to know that candidate will not fail before/at other failed options
- Choice continuation is deeply nested 
- `describedAs()` wraps parsing of entire remaining input 
- Discard buffered input once it is not required
- Match common prefix for choice

Features
- Push parse indicates that parsing has already failed, prior to end of input being received
- Parse file or input stream
- Match location/region
- Semantic errors
- Sync on parse failure
- Left recursion