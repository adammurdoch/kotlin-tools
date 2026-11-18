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

internal class UnexpectedArgument(val argument: String, val expected: List<String> = emptyList()) : Problem {
    override fun writeTo(output: Output) {
        output.write("Unexpected argument ")
        output.userInput(argument)
        if (expected.isNotEmpty()) {
            output.write(". Expected ")
            expected.forEachIndexed { index, name ->
                if (index > 0) {
                    if (index == expected.lastIndex) {
                        output.write(" or ")
                    } else {
                        output.write(", ")
                    }
                }
                output.write("'$name'")
            }
            output.write(" instead")
        }
    }
}

internal class RepeatedOption(val option: String, val previousOption: String? = null) : Problem {
    override fun writeTo(output: Output) {
        if (previousOption == null) {
            output.write("Option $option can only appear once")
        } else {
            output.write("Option $option cannot be used with $previousOption")
        }
    }
}

internal class MissingOptionValue(val option: String) : Problem {
    override fun writeTo(output: Output) {
        output.write("No value provided for option $option")
    }
}

internal class MissingOption(val option: String) : Problem {
    override fun writeTo(output: Output) {
        output.write("Option $option not provided")
    }
}

internal class MissingPositional(val name: String) : Problem {
    override fun writeTo(output: Output) {
        output.write("Parameter <$name> not provided")
    }
}

internal class MissingCommand(val name: String) : Problem {
    override fun writeTo(output: Output) {
        output.write("Expected '$name'")
    }
}

internal class MissingCommands(val names: List<String>) : Problem {
    override fun writeTo(output: Output) {
        output.write("Expected ")
        names.forEachIndexed { index, name ->
            if (index > 0) {
                if (index == names.lastIndex) {
                    output.write(" or ")
                } else {
                    output.write(", ")
                }
            }
            output.write("'$name'")
        }
    }
}