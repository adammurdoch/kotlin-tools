package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.NativeMachine
import org.gradle.api.provider.Provider

class ComponentTargets(
    val jvm: Provider<Int>?,
    val nativeTargets: Set<NativeMachine>,
    val browser: Boolean = false
)