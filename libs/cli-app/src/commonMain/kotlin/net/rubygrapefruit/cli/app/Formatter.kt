package net.rubygrapefruit.cli.app

internal abstract class Formatter {
    private var atStartOfLine = true
    private var depth = 0

    fun append(value: String?) {
        if (!value.isNullOrEmpty()) {
            var pos = 0
            while (pos < value.length) {
                val nextPos = value.indexOf('\n', pos)
                if (nextPos < 0) {
                    line(value.substring(pos))
                    break
                }
                if (nextPos > pos) {
                    line(value.substring(pos, nextPos))
                }
                newLine()
                pos = nextPos + 1
            }
        }
    }

    fun appendln(value: String?) {
        if (!value.isNullOrEmpty()) {
            append(value)
        }
        newLine()
    }

    fun maybeNewLine() {
        if (!atStartOfLine) {
            newLine()
        }
    }

    fun newLine() {
        write("\n")
        atStartOfLine = true
    }

    fun <T> indent(builder: Formatter.() -> T): T {
        depth++
        try {
            return builder()
        } finally {
            depth--
        }
    }

    fun <T> table(title: String, items: List<T>, row: (T) -> Pair<String, String?>) {
        if (items.isNotEmpty()) {
            newLine()
            append("$title:")
            newLine()
            val width = items.maxOf { row(it).first.length }
            for (item in items) {
                val cells = row(item)
                append("  ")
                val second = cells.second
                if (second != null) {
                    append(cells.first.padEnd(width))
                    append(" ")
                    append(second)
                } else {
                    append(cells.first)
                }
                newLine()
            }
        }
    }

    private fun line(text: String) {
        if (atStartOfLine) {
            for (i in 0 until depth) {
                write("    ")
            }
            atStartOfLine = false
        }
        write(text)
    }

    protected abstract fun write(value: String)
}

internal object LoggingFormatter : Formatter() {
    override fun write(value: String) {
        print(value)
    }
}