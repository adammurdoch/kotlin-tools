package net.rubygrapefruit.cli.app

internal abstract class Formatter {
    private var atStartOfLine = true

    fun append(value: String?) {
        if (!value.isNullOrEmpty()) {
            text(value)
            atStartOfLine = value.endsWith('\n')
        }
    }

    fun maybeNewLine() {
        if (!atStartOfLine) {
            newLine()
        }
    }

    fun newLine() {
        text("\n")
        atStartOfLine = true
    }

    fun <T> table(title: String, items: List<T>, row: (T) -> Pair<String, String?>) {
        if (items.isNotEmpty()) {
            newLine()
            text("$title:")
            newLine()
            val width = items.maxOf { row(it).first.length }
            for (item in items) {
                val cells = row(item)
                text("  ")
                val second = cells.second
                if (second != null) {
                    text(cells.first.padEnd(width))
                    text(" ")
                    text(second)
                } else {
                    text(cells.first)
                }
                newLine()
            }
        }
    }

    protected abstract fun text(value: String)
}

internal object LoggingFormatter : Formatter() {
    override fun text(value: String) {
        print(value)
    }
}