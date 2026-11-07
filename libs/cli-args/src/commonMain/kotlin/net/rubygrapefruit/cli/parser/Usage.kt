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
    sealed interface OneOf : UsageInternal {
        fun choices(): List<Usage>
    }
}
