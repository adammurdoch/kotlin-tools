package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.internal.component.MutableComponent

/**
 * A component with more than one target.
 */
interface HasTargets {
    val common: HasDependencies

    fun visitTargets(consumer: (MutableComponent) -> Unit)
}