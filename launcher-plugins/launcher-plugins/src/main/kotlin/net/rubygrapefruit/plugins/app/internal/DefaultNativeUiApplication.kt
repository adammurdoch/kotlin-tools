package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.BuildType
import net.rubygrapefruit.plugins.app.NativeMachine
import net.rubygrapefruit.plugins.app.NativeUIApplication
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ProviderFactory
import javax.inject.Inject

abstract class DefaultNativeUiApplication @Inject constructor(
    objects: ObjectFactory,
    providers: ProviderFactory,
    project: Project
) : DefaultUiApplication(objects, providers, project), MutableNativeApplication, NativeUIApplication {
    init {
        targets.add(NativeMachine.MacOSArm64, listOf(BuildType.Debug, BuildType.Release), DefaultHasLauncherExecutableDistribution::class.java)
        targets.add(NativeMachine.MacOSX64, listOf(BuildType.Debug, BuildType.Release), DefaultHasLauncherExecutableDistribution::class.java)
    }
}