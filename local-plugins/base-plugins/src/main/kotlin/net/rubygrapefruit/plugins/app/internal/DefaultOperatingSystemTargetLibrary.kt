package net.rubygrapefruit.plugins.app.internal

import javax.inject.Inject

abstract class DefaultOperatingSystemTargetLibrary @Inject constructor(override val target: OperatingSystem) : DefaultNativeLibrary(target.mainSourceSetName), HasOsTarget
