package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.*
import net.rubygrapefruit.plugins.app.internal.component.ComponentFactory
import net.rubygrapefruit.plugins.app.internal.component.MutableComponent
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

internal abstract class DefaultMultiPlatformLibrary @Inject constructor(
    private val componentRegistry: MultiPlatformComponentRegistry,
    private val factory: ObjectFactory,
    private val project: Project,
    private val componentFactory: ComponentFactory
) : MultiPlatformLibrary, MutableComponent, HasTargets {
    private val commonSource = DefaultLibrarySourceSet("commonMain", generatedSource)
    private val commonTest = DefaultHasDependencies("commonTest")
    private val common = DefaultPlatformContribution(commonSource, commonTest)
    private val osComponents = mutableMapOf<OperatingSystem, DefaultNativeLibrary>()
    private var jvm: DefaultJvmLibrary? = null
    private var browser: DefaultBrowserLibrary? = null
    private var desktop: DefaultNativeLibrary? = null

    val module: JvmModule
        get() = createJvm().module

    override fun visitPlatforms(consumer: (PlatformContribution) -> Unit) {
        consumer(common)
        if (jvm != null) {
            consumer(jvm!!)
        }
        if (browser != null) {
            consumer(browser!!)
        }
        if (desktop != null) {
            consumer(desktop!!)
        }
        for (component in osComponents.values) {
            consumer(component)
        }
    }

    override fun jvm(config: JvmLibrary.() -> Unit) {
        createJvm().config()
    }

    override fun browser() {
        createBrowser()
    }

    override fun macOS(config: NativeLibrary.() -> Unit) {
        componentRegistry.macOS()
        createForOS(OperatingSystem.MacOS).config()
    }

    override fun desktop(config: NativeLibrary.() -> Unit) {
        createDesktop().config()
    }

    override fun nativeDesktop() {
        componentRegistry.nativeDesktop()
        for (os in OperatingSystem.desktop) {
            createForOS(os)
        }
    }

    override fun common(config: LibraryDependencies.() -> Unit) {
        commonSource.dependencies.config()
    }

    override fun test(config: Dependencies.() -> Unit) {
        commonTest.dependencies.config()
    }

    private fun createDesktop(): DefaultNativeLibrary {
        if (desktop == null) {
            val lib = factory.newInstance(DefaultNativeLibrary::class.java, "desktopMain")
            componentFactory.created(lib)
            desktop = lib
        }
        return desktop!!
    }

    private fun createJvm(): JvmLibrary {
        if (jvm == null) {
            val library = factory.newInstance(DefaultJvmLibrary::class.java, "jvmMain", "jvmTest")
            componentFactory.created(library)
            library.module.name.convention(toModuleName(project.name))
            library.targetJvmVersion.convention(Versions.libs.jvm.version)
            jvm = library
            // This can call back to query JVM object
            componentRegistry.jvm()
        }
        return jvm!!
    }

    private fun createBrowser() {
        if (browser == null) {
            val library = DefaultBrowserLibrary()
            componentFactory.created(library)
            browser = library
        }
    }

    private fun createForOS(operatingSystem: OperatingSystem): DefaultNativeLibrary {
        return osComponents.getOrPut(operatingSystem) {
            val library = factory.newInstance(DefaultNativeOsLibrary::class.java, operatingSystem)
            componentFactory.created(library)
            library
        }
    }
}