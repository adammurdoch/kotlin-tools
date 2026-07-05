package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.internal.component.MutableComponent

interface HasTargets {
    fun visitTargets(consumer: (MutableComponent) -> Unit)
}