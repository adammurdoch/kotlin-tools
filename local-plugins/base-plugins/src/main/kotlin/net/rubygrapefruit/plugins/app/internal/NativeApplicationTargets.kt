package net.rubygrapefruit.plugins.app.internal

import org.gradle.api.file.Directory
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.SetProperty

class NativeApplicationTargets(
    private val objects: ObjectFactory,
    generatedSource: SetProperty<Directory>
) {
    private val commonMain = DefaultSourceSet("commonMain", DefaultDependencies(), generatedSource)
    private val commonTest = DefaultHasDependencies("commonTest")
    val common = DefaultPlatformContribution(commonMain, commonTest)
    private var macOS: DefaultNativeComponent? = null

    fun visitPlatforms(consumer: (PlatformContribution) -> Unit) {
        consumer(common)
        if (macOS != null) {
            consumer(macOS!!)
        }
    }

    fun macOS(): DefaultNativeComponent {
        if (macOS == null) {
            val component = objects.newInstance(DefaultNativeComponent::class.java, "macosMain")
            macOS = component
        }
        return macOS!!
    }
}