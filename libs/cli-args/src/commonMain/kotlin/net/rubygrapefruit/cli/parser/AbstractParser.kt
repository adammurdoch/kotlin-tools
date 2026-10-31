package net.rubygrapefruit.cli.parser

internal abstract class AbstractParser<out T> : Parser<T>, ParserInternal<T> {
    override fun parse(args: List<String>): Parser.Result<T> {
        val result = maybeParse(args)
        return when (result) {
            is ParserInternal.Failure -> result
            is ParserInternal.Success -> {
                if (result.count == args.size) {
                    result
                } else {
                    stoppedAt(result.count, args)
                }
            }

            is ParserInternal.NotRecognized -> stoppedAt(0, args)
        }
    }

    private fun stoppedAt(index: Int, args: List<String>): ParserInternal.Failure {
        val arg = args[index]
        return if (arg.startsWith("-")) {
            ParserInternal.Failure(index, "Unexpected option '$arg'")
        } else {
            ParserInternal.Failure(index, "Unexpected parameter '$arg'")
        }
    }
}