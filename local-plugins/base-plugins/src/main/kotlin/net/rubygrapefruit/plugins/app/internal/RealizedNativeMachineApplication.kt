package net.rubygrapefruit.plugins.app.internal

import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

class RealizedNativeMachineApplication(
    val canBuildOnHost: Boolean,
    val machine: KotlinTargetMachine,
    val entryPoint: Property<String>,
    val declaredAppEntryPoint: Provider<String>,
    val target: KotlinNativeTarget
)
