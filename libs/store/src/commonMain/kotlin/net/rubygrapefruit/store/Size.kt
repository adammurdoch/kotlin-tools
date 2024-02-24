package net.rubygrapefruit.store

import kotlin.jvm.JvmInline

@JvmInline
value class Size(val value: Int) {
    constructor(value: Long) : this(value.toInt())
}
