package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

data class ProblemAtIndex(val index: Int, val problem: Problem)

sealed interface Problem {
    fun writeTo(output: Output)
}

internal class BadlyFormedOption(val option: String, val suggestion: String) : Problem {
    override fun writeTo(output: Output) {
        output.write("Unexpected option ")
        output.userInput(option)
        output.write(". Try ")
        output.userInput(suggestion)
        output.write(" instead")
    }
}

internal class BadlyFormedOptionValue(val option: String, val value: String, val expected: List<String> = emptyList()) : Problem {
    override fun writeTo(output: Output) {
        output.write("Unexpected argument ")
        output.userInput(value)
        output.write(" for option $option")
        if (expected.isNotEmpty()) {
            output.write(". Expected ")
            output.list(expected)
            output.write(" instead")
        }
    }
}

internal class MissingOptionValue(val option: String) : Problem {
    override fun writeTo(output: Output) {
        output.write("No value provided for option $option")
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

internal class UnexpectedOption(val option: String, val expected: List<String> = emptyList()) : Problem {
    override fun writeTo(output: Output) {
        output.write("Unexpected option ")
        output.userInput(option)
        if (expected.isNotEmpty()) {
            output.write(". Expected ")
            output.list(expected)
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
            output.list(expected)
            output.write(" instead")
        }
    }
}

internal class MissingParameters(val descriptions: List<String>) : Problem {
    override fun writeTo(output: Output) {
        output.write("Expected ")
        output.list(descriptions)
    }
}

private fun Output.list(descriptions: List<String>) {
    descriptions.forEachIndexed { index, description ->
        if (index > 0) {
            if (index == descriptions.lastIndex) {
                write(" or ")
            } else {
                write(", ")
            }
        }
        write(description)
    }
}