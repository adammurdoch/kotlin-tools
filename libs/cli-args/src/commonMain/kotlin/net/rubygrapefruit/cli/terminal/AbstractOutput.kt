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

    override fun definitionList(builder: Output.Definitions.() -> Unit) {
        val definitions = DefaultDefinitions(this)
        definitions.builder()
        if (definitions.items.isEmpty()) {
            return
        }

        val termsWidth = definitions.items.maxOf { it.termLength }
        for (definition in definitions.items) {
            put("  ", Appearance.Normal)
            for (span in definition.term) {
                put(span.text, span.appearance)
            }
            if (definition.description.isNotEmpty()) {
                val diff = termsWidth - definition.termLength
                repeat(diff + 2) {
                    put(" ", Appearance.Normal)
                }
                for (span in definition.description) {
                    put(span.text, span.appearance)
                }
            }
            writeLine()
        }
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

    private class Span(val text: String, val appearance: Appearance)

    private class DefaultDefinitions(val owner: AbstractOutput) : Output.Definitions {
        val items: List<DefaultDefinition>
            field = mutableListOf()

        override fun item(builder: Output.Definition.() -> Unit) {
            val definition = DefaultDefinition(owner)
            definition.builder()
            items.add(definition)
        }
    }

    private class DefaultDefinition(val owner: AbstractOutput) : Output.Definition {
        var term: List<Span> = emptyList()
            private set
        var termLength: Int = 0
            private set
        var description: List<Span> = emptyList()
            private set

        override fun term(builder: Output.() -> Unit) {
            val output = RecordingOutput()
            output.builder()
            term = output.spans
            termLength = term.sumOf { it.text.length }
        }

        override fun description(builder: Output.() -> Unit) {
            val output = RecordingOutput()
            output.builder()
            description = output.spans
        }
    }

    private class RecordingOutput : AbstractOutput() {
        val spans: List<Span>
            field = mutableListOf<Span>()

        override fun put(text: CharSequence, appearance: Appearance) {
            if (text.isNotEmpty()) {
                spans.add(Span(text.toString(), appearance))
            }
        }
    }
}