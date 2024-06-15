package net.rubygrapefruit.cli

internal fun StringBuilder.appendItems(items: List<ItemUsage>) {
    val nameWidth = items.maxOf { it.display.length }
    val sorted = items.sortedBy { it.display }
    for (index in sorted.indices) {
        val action = sorted[index]
        if (index > 0) {
            append("\n")
        }
        if (action.help != null) {
            val padded = action.display.padEnd(nameWidth)
            append("  $padded ${action.help}")
        } else {
            append("  ${action.display}")
        }
    }
}

internal fun StringBuilder.appendItems(title: String, items: List<ItemUsage>, trailingNewLine: Boolean = true) {
    if (items.isEmpty()) {
        return
    }
    if (!trailingNewLine) {
        append("\n")
    }
    append("\n$title:\n")
    appendItems(items)
    if (trailingNewLine) {
        append("\n")
    }
}