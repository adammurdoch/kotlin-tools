To add a new parser:

- Add factory functions and implementation.
  - Add these in one of `binary`, `text`, `general` or `combinators` package
  - Implement one of `CombinatorBuilder`, `TypedInputCombinatorBuilder` or `ParserBuilder`
  - Implement `DiscardableParser`
  - Alternatively, use `MatchOneInputParser` along with an `InputPredicate` implementation.
- Document the factory functions in `README.md`
- Add tests in a class that extends `AbstractParseTest`.
  - These tests should use only the factory functions, and literals if the parser is a combinator of some kind.
- Add `DiscardOf<parser>` test class.
- Add additional tests that use the parser with other combinators, as required.
