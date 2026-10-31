package net.rubygrapefruit.cli.parser

internal class DefaultOption(name: String) : AbstractParser<String?>(), Option<String?> {
    private val flag = "-$name"

    override fun maybeParse(args: List<String>): ParserInternal.Result<String?> {
        return if (args.isEmpty()) {
            ParserInternal.Success(0, null)
        } else {
            val first = args[0]
            if (first != flag) {
                ParserInternal.NotRecognized
            } else if (args.size == 1 || args.size >= 2 && args[1].startsWith("-")) {
                ParserInternal.Failure(listOf(Parser.Problem(1, "No value provided for option $first")))
            } else {
                ParserInternal.Success(2, args[1])
            }
        }
    }
}