package net.rubygrapefruit.cli.parser

internal abstract class AbstractParser<out T> : Parser<T>, ParserInternal<T> {
    override fun parse(args: List<String>): Parser.Result<T> {
        val input = InputArgs(0, args)
        val result = maybeParse(input)
        return when (result) {
            is ParserInternal.Failure -> result
            is ParserInternal.Success -> {
                if (result.count == input.length) {
                    result
                } else {
                    stoppedAt(input.drop(result.count))
                }
            }

            is ParserInternal.NotRecognized -> stoppedAt(input)
        }
    }

    private fun stoppedAt(input: InputArgs): ParserInternal.Failure {
        return EndOfParameters.maybeParse(input)
    }
}