package net.rubygrapefruit.plugins.app.internal

import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

class RealizedNativeTarget(
    val canBuildOnHost: Boolean,
    val machine: KotlinTargetMachine,
    val entryPoint: Provider<String>,
    val target: KotlinNativeTarget
)
