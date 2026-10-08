package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.BuildType

interface HasTargetMachine {
    val targetMachine: KotlinTargetMachine

    val buildType: BuildType
}