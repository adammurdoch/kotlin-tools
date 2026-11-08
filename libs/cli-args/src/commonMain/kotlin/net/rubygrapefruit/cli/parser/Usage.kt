package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

/**
 * Usage information for a given [Parser].
 */
sealed interface Usage {
    /**
     * Writes a usage summary to the given output.
     */
    fun writeTo(output: Output)
}

internal sealed interface UsageInternal : Usage {
    override fun writeTo(output: Output) {
        writeTo(false, output)
    }

    /**
     * Write usage as part of a single line summary.
     */
    fun writeTo(quoted: Boolean, output: Output)

    sealed interface OneOf : UsageInternal {
        fun choices(): List<UsageInternal>
    }
}

internal inline fun <T> writeChoices(quoted: Boolean, output: Output, choices: List<T>, writer: (T) -> Unit) {
    when (choices.size) {
        0 -> {
            // Ignore
        }

        1 -> writer(choices.first())

        else -> {
            if (quoted) {
                output.write("(")
            }
            choices.forEachIndexed { index, choice ->
                if (index > 0) {
                    output.write(" | ")
                }
                writer(choice)
            }
            if (quoted) {
                output.write(")")
            }
        }
    }
}
