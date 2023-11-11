package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.MultiPlatformLibrary
import net.rubygrapefruit.plugins.app.NativeMachine
import net.rubygrapefruit.plugins.bootstrap.Versions
import org.gradle.api.internal.provider.Providers
import javax.inject.Inject

internal open class DefaultMultiPlatformLibrary @Inject constructor(
    private val componentRegistry: MultiPlatformComponentRegistry
): MultiPlatformLibrary {
    private var jvm = false

    override fun jvm() {
        jvm = true
    }

    override fun nativeDesktop() {
        componentRegistry.registerSourceSets(ComponentTargets(Providers.of(Versions.java), setOf(NativeMachine.LinuxX64, NativeMachine.MacOSX64, NativeMachine.MacOSArm64, NativeMachine.WindowsX64)))
    }
}