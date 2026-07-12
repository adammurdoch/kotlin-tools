package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.internal.component.ComponentFactory
import org.gradle.api.file.Directory
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.SetProperty

class NativeApplicationTargets(
    private val objects: ObjectFactory,
    private val componentFactory: ComponentFactory,
    generatedSource: SetProperty<Directory>
) {
    private val commonMain = DefaultSourceSet("commonMain", generatedSource)
    private val commonTest = DefaultHasDependencies("commonTest")
    val common = DefaultPlatformContribution(commonMain, commonTest)
    private val osComponents = mutableMapOf<OperatingSystem, DefaultNativeComponent>()

    fun visitPlatforms(consumer: (PlatformContribution) -> Unit) {
        consumer(common)
        for (component in osComponents.values) {
            consumer(component)
        }
    }

    fun macOS(): DefaultNativeComponent {
        return forOS(OperatingSystem.MacOS)
    }

    fun nativeDesktop() {
        for (os in OperatingSystem.desktop) {
            forOS(os)
        }
    }

    private fun forOS(operatingSystem: OperatingSystem): DefaultNativeComponent {
        return osComponents.getOrPut(operatingSystem) {
            val component = objects.newInstance(DefaultNativeComponent::class.java, operatingSystem.mainSourceSetName)
            componentFactory.created(component)
            component
        }
    }
}