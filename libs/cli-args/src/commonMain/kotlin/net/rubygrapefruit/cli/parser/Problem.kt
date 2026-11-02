package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

data class ProblemAtIndex(val index: Int, val problem: Problem)

sealed interface Problem {
    fun writeTo(output: Output)
}

internal class UnexpectedOption(val option: String, val suggestion: String? = null) : Problem {
    override fun writeTo(output: Output) {
        output.write("Unexpected option ")
        output.userInput(option)
        if (suggestion != null) {
            output.write(". Try ")
            output.userInput(suggestion)
            output.write(" instead")
        }
    }
}

internal class UnexpectedArgument(val argument: String) : Problem {
    override fun writeTo(output: Output) {
        output.write("Unexpected argument ")
        output.userInput(argument)
    }
}

internal class RepeatedOption(val option: String) : Problem {
    override fun writeTo(output: Output) {
        output.write("Option $option can only appear once")
    }
}

internal class MissingOptionValue(val option: String) : Problem {
    override fun writeTo(output: Output) {
        output.write("No value provided for option $option")
    }
}

internal class MissingParameter(val name: String) : Problem {
    override fun writeTo(output: Output) {
        output.write("Parameter <$name> not provided")
    }
}