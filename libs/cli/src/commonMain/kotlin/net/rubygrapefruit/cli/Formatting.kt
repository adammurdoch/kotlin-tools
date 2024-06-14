package net.rubygrapefruit.cli

internal fun StringBuilder.appendItems(items: List<ItemUsage>) {
    val nameWidth = items.maxOf { it.display.length }
    for (index in items.indices) {
        val action = items[index]
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