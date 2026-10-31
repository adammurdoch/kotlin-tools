package net.rubygrapefruit.cli.parser

internal class DefaultOption(private val name: String) : Option<String?> {
    override fun parse(args: List<String>): Parser.Result<String?> {
        return if (args.isEmpty()) {
            Parser.Success(null)
        } else if (args.size == 2 && args[0] == "-$name" && !args[1].startsWith("-")) {
            Parser.Success(args[1])
        } else {
            Parser.Failure
        }
    }
}