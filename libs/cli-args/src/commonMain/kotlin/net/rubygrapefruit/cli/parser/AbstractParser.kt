package net.rubygrapefruit.cli.parser

internal abstract class AbstractParser<out T> : Parser<T>, ParserInternal<T> {
    override fun parse(args: List<String>): Parser.Result<T> {
        var current = InputArgs(0, args)
        val state = start()
        while (true) {
            val result = state.maybeParse(current)
            when (result) {
                is ParseState.NotRecognized -> break
                is ParseState.Success -> current = current.drop(result.count)
                is ParseState.Failure ->
                    // Should attempt to recover and continue
                    return result
            }
        }

        if (!current.isEmpty) {
            // Should merge with state's final result
            val first = current.first()
            return if (first.startsWith("-")) {
                ParseState.Failure(current.index, "Unexpected option '$first'")
            } else {
                ParseState.Failure(current.index, "Unexpected argument '$first'")
            }
        }

        return state.finish()
    }
}