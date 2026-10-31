package net.rubygrapefruit.cli.parser

internal class DefaultOption(name: String) : AbstractParser<String?>(), Option<String?> {
    private val flag = "-$name"

    override fun maybeParse(args: InputArgs): ParserInternal.Result<String?> {
        return if (args.isEmpty) {
            ParserInternal.Success(0, null)
        } else {
            val first = args.first()
            if (first != flag) {
                ParserInternal.NotRecognized
            } else if (args.length == 1 || args.length >= 2 && args.second().startsWith("-")) {
                ParserInternal.Failure(listOf(Parser.Problem(args.index + 1, "No value provided for option $first")))
            } else {
                ParserInternal.Success(2, args.second())
            }
        }
    }
}