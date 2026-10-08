package net.rubygrapefruit.plugins.app.internal

import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

class RealizedNativeTarget(val canBuildOnHost: Boolean, val machine: KotlinTargetMachine, val target: KotlinNativeTarget)
