package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.BuildType
import net.rubygrapefruit.plugins.app.NativeMachine
import net.rubygrapefruit.plugins.app.UiApplication
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory

abstract class DefaultUiApplication(
    objects: ObjectFactory,
    providers: ProviderFactory,
    project: Project
) : MutableApplication, UiApplication {
    protected val targets = NativeTargetsContainer(objects, providers, project.tasks)

    final override val distributionContainer = targets.distributions

    val capitalizedAppName: Provider<String> = appName.map { it.replaceFirstChar { it.uppercase() } }

    val iconName: Provider<String> = capitalizedAppName.map { "$it.icns" }

    fun attachExecutable(machine: NativeMachine, buildType: BuildType, binaryFile: Provider<RegularFile>) {
        targets.attachExecutable(machine, buildType, binaryFile)
    }

    fun eachTarget(action: (NativeMachine, DefaultDistribution) -> Unit) {
        targets.eachTarget(action)
    }
}