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
    val isEmpty: Boolean
        get() = false

    /**
     * Unpacks this usage into its choices, if any.
     *
     * Returns a list with a single element if this usage has no choices
     */
    fun choices(): List<UsageInternal> {
        return listOf(this)
    }

    fun options(): List<OptionUsage> {
        return emptyList()
    }

    fun writeSummaryTo(output: Output) {
        val choices = choices()
        if (choices.size > 1) {
            output.heading("USAGE:")
            output.writeLine()
            for (choice in choices) {
                output.write("  ")
                choice.writeTo(false, output)
                output.writeLine()
            }
        } else {
            output.heading("USAGE:")
            output.write(" ")
            writeTo(false, output)
            output.writeLine()
        }
    }

    override fun writeTo(output: Output) {
        choices().forEachIndexed { index, choice ->
            if (index > 0) {
                output.writeLine()
            }
            choice.writeTo(false, output)
        }
    }

    /**
     * Write usage as part of a single line summary.
     */
    fun writeTo(quoted: Boolean, output: Output)

    sealed interface OptionUsage : UsageInternal {
        override fun options(): List<OptionUsage> {
            return listOf(this)
        }

        fun writeOptionUsageTo(output: Output)
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

internal inline fun <T> writeList(output: Output, items: List<T>, writer: (T) -> Unit) {
    items.forEachIndexed { index, item ->
        if (index > 0) {
            output.write(", ")
        }
        writer(item)
    }
}
