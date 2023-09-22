package sample.calc

class Parser {
    fun parse(args: Array<String>): ParseResult {
        val combined = args.joinToString(" ")
        val result = parseExpression(combined)
        return when (result) {
            is Failure -> result
            is Success -> {
                if (result.remaining.isNotEmpty()) {
                    Failure("end of text", result.remaining)
                } else {
                    result
                }
            }
        }
    }

    private fun parseExpression(text: String): ParseResult {
        val matchAdd = parseAddition(text)
        if (matchAdd is Success) {
            return matchAdd
        }
        val matchNumber = parseNumber(text)
        if (matchNumber is Success && matchNumber.remaining.length < matchAdd.remaining.length) {
            return matchNumber
        }
        if (matchAdd.remaining.length < matchNumber.remaining.length) {
            return matchAdd
        } else {
            return matchNumber
        }
    }

    private fun parseAddition(text: String): ParseResult {
        val matchLeft = parseNumber(text)
        if (matchLeft is Failure) {
            return matchLeft
        }
        require(matchLeft is Success)
        if (matchLeft.remaining.isEmpty() || matchLeft.remaining.first() != '+') {
            return Failure("a '+' character", matchLeft.remaining)
        }
        val matchRight = parseNumber(matchLeft.remaining.substring(1))
        if (matchRight is Failure) {
            return matchRight
        }
        require(matchRight is Success)
        return Success(Addition(matchLeft.expression, matchRight.expression), matchRight.remaining)
    }

    private fun parseNumber(text: String): ParseResult {
        val digit = Regex("\\s*(\\d+)\\s*")
        val result = digit.matchAt(text, 0)
        return if (result == null) {
            Failure("a number", text.trimStart())
        } else {
            Success(Number(result.groups[1]!!.value.toInt()), text.substring(result.range.last + 1))
        }
    }
}