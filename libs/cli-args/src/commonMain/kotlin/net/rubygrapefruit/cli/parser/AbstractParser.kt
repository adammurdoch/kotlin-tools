package net.rubygrapefruit.cli.parser

internal abstract class AbstractParser<out T> : Parser<T>, ParserInternal<T> {
    override fun parse(args: List<String>): Parser.Result<T> {
        var current = InputArgs(0, args)
        val state = start()
        var failure: Parser.Failure? = null
        while (!current.isEmpty) {
            val result = state.maybeParse(current)
            when (result) {
                is ParseState.NotRecognized -> {
                    val first = current.first()
                    val message = if (first.startsWith("-")) {
                        "Unexpected option '$first'"
                    } else {
                        "Unexpected argument '$first'"
                    }
                    failure = failure.plus(current.index, message)
                    // Attempt to recover and continue
                    current = current.drop(1)
                }

                is ParseState.Success -> {
                    require(result.count > 0)
                    current = current.drop(result.count)
                }
                is ParseState.Failure -> {
                    require(result.count > 0)
                    failure = failure.plus(current.index + result.index, result.message)
                    // Attempt to recover and continue
                    current = current.drop(result.count)
                }
            }
        }

        val result = state.finish()
        return when (result) {
            is Parser.Failure -> {
                failure.plus(result)
            }

            is Parser.Success -> {
                if (failure != null) {
                    failure
                } else {
                    result
                }
            }
        }
    }
}