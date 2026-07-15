package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.NativeMachine
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

class RealizedNativeTarget(val canBuildOnHost: Boolean, val machine: NativeMachine, val target: KotlinNativeTarget)
