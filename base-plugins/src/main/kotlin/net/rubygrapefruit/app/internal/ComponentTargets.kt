package net.rubygrapefruit.app.internal

import net.rubygrapefruit.app.NativeMachine
import org.gradle.api.provider.Provider

class ComponentTargets(
    val jvm: Provider<Int>?,
    val nativeTargets: Set<NativeMachine>,
)