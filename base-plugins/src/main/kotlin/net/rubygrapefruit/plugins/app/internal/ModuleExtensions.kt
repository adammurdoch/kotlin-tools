package net.rubygrapefruit.plugins.app.internal

internal fun toModuleName(value: String) =
    value.replace(Regex("[ \\-](\\w)")) { matchResult -> matchResult.groups[1]!!.value.uppercase() }
