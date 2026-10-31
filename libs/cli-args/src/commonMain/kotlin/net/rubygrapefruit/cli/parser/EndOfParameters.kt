package net.rubygrapefruit.cli.parser

internal object EndOfParameters : ParserInternal<Nothing> {
    override fun maybeParse(args: InputArgs): ParserInternal.Failure {
        val arg = args.first()
        return if (arg.startsWith("-")) {
            ParserInternal.Failure(args.index, "Unexpected option '$arg'")
        } else {
            ParserInternal.Failure(args.index, "Unexpected parameter '$arg'")
        }
    }
}