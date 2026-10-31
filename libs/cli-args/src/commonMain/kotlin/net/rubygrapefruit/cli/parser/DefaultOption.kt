package net.rubygrapefruit.cli.parser

internal class DefaultOption(name: String) : Option<String?> {
    private val flag = "-$name"

    override fun parse(args: List<String>): Parser.Result<String?> {
        return if (args.isEmpty()) {
            Parser.Success(null)
        } else {
            val first = args[0]
            if (first != flag) {
                if (first.startsWith("-")) {
                    Parser.Failure(listOf(Parser.Problem(0, "Unexpected option '$first'")))
                } else {
                    Parser.Failure(listOf(Parser.Problem(0, "Unexpected parameter '$first'")))
                }
            } else if (args.size == 1 || args.size == 2 && args[1].startsWith("-")) {
                Parser.Failure(listOf(Parser.Problem(1, "No value provided for option $first")))
            } else if (args.size == 2) {
                Parser.Success(args[1])
            } else {
                val third = args[2]
                if (third == flag) {
                    Parser.Failure(listOf(Parser.Problem(2, "Option $flag can only appear once")))
                } else if (third.startsWith("-")) {
                    Parser.Failure(listOf(Parser.Problem(2, "Unexpected option '$third'")))
                } else {
                    Parser.Failure(listOf(Parser.Problem(2, "Unexpected parameter '$third'")))
                }
            }
        }
    }
}