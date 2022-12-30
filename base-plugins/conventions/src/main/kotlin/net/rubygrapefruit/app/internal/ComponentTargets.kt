package net.rubygrapefruit.app.internal

import net.rubygrapefruit.app.NativeMachine

class ComponentTargets(
    val jvm: Boolean,
    val nativeTargets: Set<NativeMachine>,
    // When false, applies workaround for duplicate native symbol problem
    // TODO - remove this
    val testSourceSets: Boolean = true
)