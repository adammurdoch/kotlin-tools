package net.rubygrapefruit.app.internal

import net.rubygrapefruit.app.NativeMachine

class ComponentTargets(
    val jvm: Boolean,
    val nativeTargets: Set<NativeMachine>,
)