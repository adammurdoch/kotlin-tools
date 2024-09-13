package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.BuildType
import net.rubygrapefruit.plugins.app.NativeMachine

interface HasTargetMachine {
    val targetMachine: NativeMachine

    val buildType: BuildType
}