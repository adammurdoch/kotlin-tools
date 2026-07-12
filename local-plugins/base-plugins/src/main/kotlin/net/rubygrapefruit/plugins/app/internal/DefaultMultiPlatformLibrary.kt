package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.*
import net.rubygrapefruit.plugins.app.internal.component.MutableComponent
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

internal abstract class DefaultMultiPlatformLibrary @Inject constructor(
    private val componentRegistry: MultiPlatformComponentRegistry,
    private val factory: ObjectFactory,
    private val project: Project
) : MultiPlatformLibrary, MutableComponent, HasTargets {
    private var jvm: DefaultJvmLibrary? = null
    private val commonSource = DefaultLibrarySourceSet("commonMain", generatedSource)
    private val commonTest = DefaultHasDependencies("commonTest")
    private val common = DefaultPlatformContribution(commonSource, commonTest)
    private val osComponents = mutableMapOf<OperatingSystem, DefaultNativeLibrary>()

    val module: JvmModule
        get() = createJvm().module

    override fun visitPlatforms(consumer: (PlatformContribution) -> Unit) {
        consumer(common)
        if (jvm != null) {
            consumer(jvm!!)
        }
        for (component in osComponents.values) {
            consumer(component)
        }
    }

    override fun jvm() {
        jvm {}
    }

    override fun jvm(config: JvmLibrary.() -> Unit) {
        createJvm().config()
    }

    override fun browser() {
        componentRegistry.browser()
    }

    override fun macOS() {
        macOS {}
    }

    override fun macOS(config: NativeLibrary.() -> Unit) {
        componentRegistry.macOS()
        forOS(OperatingSystem.MacOS).config()
    }

    override fun desktop(config: NativeLibrary.() -> Unit) {
        val lib = factory.newInstance(DefaultNativeLibrary::class.java, "desktopMain")
        lib.config()
    }

    override fun nativeDesktop() {
        componentRegistry.nativeDesktop()
        for (os in OperatingSystem.desktop) {
            forOS(os)
        }
    }

    override fun common(config: LibraryDependencies.() -> Unit) {
        commonSource.dependencies.config()
    }

    override fun test(config: Dependencies.() -> Unit) {
        commonTest.dependencies.config()
    }

    private fun createJvm(): JvmLibrary {
        if (jvm == null) {
            val lib = factory.newInstance(DefaultJvmLibrary::class.java, "jvmMain", "jvmTest")
            lib.module.name.convention(toModuleName(project.name))
            lib.targetJvmVersion.convention(Versions.libs.jvm.version)
            jvm = lib
            // This can call back to query JVM object
            componentRegistry.jvm()
        }
        return jvm!!
    }

    private fun forOS(operatingSystem: OperatingSystem): DefaultNativeLibrary {
        return osComponents.getOrPut(operatingSystem) { factory.newInstance(DefaultNativeLibrary::class.java, operatingSystem.mainSourceSetName) }
    }
}