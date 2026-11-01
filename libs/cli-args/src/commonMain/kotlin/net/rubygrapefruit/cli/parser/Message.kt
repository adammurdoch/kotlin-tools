package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

/**
 * A structured message.
 */
sealed interface Message {
    fun writeTo(output: Output)

    class Text(val text: String) : Message {
        override fun writeTo(output: Output) {
            output.write(text)
        }

        operator fun plus(other: Message) = Para(listOf(this, other))
    }

    class UserInput(val text: String) : Message {
        override fun writeTo(output: Output) {
            output.userInput(text)
        }
    }

    class Para(val spans: List<Message>) : Message {
        override fun writeTo(output: Output) {
            for (span in spans) {
                span.writeTo(output)
            }
        }
    }
}