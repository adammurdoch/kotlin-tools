package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

internal class PositionalUsage(private val name: String, private val summary: String?) : UsageInternal.PositionalUsage {
    override fun writeTo(quoted: Boolean, output: Output) {
        output.write("<$name>")
    }

    override fun writePositionalUsageTo(output: Output) {
        output.write("<$name>")
    }

    override fun writePositionalSummaryTo(output: Output) {
        output.writeNullable(summary)
    }
}

internal class OneOrMorePositionalUsage(private val name: String, private val summary: String?) : UsageInternal.PositionalUsage {
    override fun writeTo(quoted: Boolean, output: Output) {
        output.write("<$name>...")
    }

    override fun writePositionalUsageTo(output: Output) {
        output.write("<$name>")
    }

    override fun writePositionalSummaryTo(output: Output) {
        output.writeNullable(summary)
    }
}

internal class ZeroOrMorePositionalUsage(private val name: String, private val summary: String?) : UsageInternal.PositionalUsage {
    override fun writeTo(quoted: Boolean, output: Output) {
        output.write("[<$name>...]")
    }

    override fun writePositionalUsageTo(output: Output) {
        output.write("<$name>")
    }

    override fun writePositionalSummaryTo(output: Output) {
        output.writeNullable(summary)
    }
}
