package net.rubygrapefruit.cli.terminal

abstract class AbstractOutput : Output {
    private var emptyLine = true

    override fun write(text: String) {
        write(text, Appearance.Normal)
    }

    override fun userInput(text: String) {
        write(text, Appearance.UserInput)
    }

    override fun heading(text: String) {
        write(text, Appearance.Heading)
    }

    override fun writeLine(text: String) {
        put(text, Appearance.Normal)
        writeLine()
    }

    override fun writeLine() {
        putEndLine()
        emptyLine = true
    }

    override fun maybeNewLine() {
        if (!emptyLine) {
            writeLine()
        }
    }

    override fun table(builder: Output.Table.() -> Unit) {
        DefaultTable(this).builder()
    }

    private fun write(text: String, appearance: Appearance) {
        put(text, appearance)
        emptyLine = text.endsWith("\n")
    }

    protected abstract fun put(text: CharSequence, appearance: Appearance)

    protected open fun putEndLine() {
        put("\n", Appearance.Normal)
    }

    protected enum class Appearance {
        Normal, UserInput, Heading
    }

    private class DefaultTable(val owner: AbstractOutput) : Output.Table {
        override fun row(builder: Output.Row.() -> Unit) {
            DefaultRow(owner).builder()
            owner.writeLine()
        }
    }

    private class DefaultRow(val owner: AbstractOutput) : Output.Row {
        var emptyCells = 0

        override fun cell(builder: Output.() -> Unit) {
            val output = RecordingOutput()
            output.builder()
            if (output.spans.isEmpty()) {
                emptyCells++
            } else {
                repeat(emptyCells + 1) {
                    owner.write("  ")
                }
                emptyCells = 0
                for (span in output.spans) {
                    owner.write(span.text, span.appearance)
                }
            }
        }
    }

    private class RecordingOutput : AbstractOutput() {
        val spans: List<Span>
            field = mutableListOf<Span>()

        override fun put(text: CharSequence, appearance: Appearance) {
            spans.add(Span(text.toString(), appearance))
        }

        class Span(val text: String, val appearance: Appearance)
    }
}