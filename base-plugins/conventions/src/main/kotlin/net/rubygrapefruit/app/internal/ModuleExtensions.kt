package net.rubygrapefruit.app.internal

internal fun toModuleName(value: String) = value.replace(Regex("-(\\w)")) { matchResult -> matchResult.groups[1]!!.value.toUpperCase() }
