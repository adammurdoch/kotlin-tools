package sample.calc

sealed class ParseResult(val remaining: String)

class Success(val expression: Expression, remaining: String) : ParseResult(remaining)

class Failure(val expected: String, remaining: String) : ParseResult(remaining) {
    val message: String
        get() {
            return "Expected ${expected}, found: ${if (remaining.isEmpty()) "''" else remaining.take(30)}"
        }
}
