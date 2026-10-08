package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.*
import net.rubygrapefruit.plugins.app.internal.component.ComponentFactory
import net.rubygrapefruit.plugins.app.internal.component.MutableComponent
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

internal abstract class DefaultMultiPlatformLibrary @Inject constructor(
    private val componentRegistry: MultiPlatformComponentRegistry,
    private val factory: ObjectFactory,
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

    override fun visitPlatforms(visitor: (PlatformContribution) -> Unit) {
        visitor(common)
        if (jvm != null) {
            visitor(jvm!!)
        }
        if (browser != null) {
            visitor(browser!!)
        }
        if (desktop != null) {
            visitor(desktop!!)
        }
        for (component in osComponents.values) {
            visitor(component)
        }
    }

    override fun jvm(config: JvmLibrary.() -> Unit) {
        createJvm().config()
    }

    override fun browser() {
        createBrowser()
    }

    override fun macOS(config: NativeLibrary.() -> Unit) {
        createForOS(OperatingSystem.MacOS).config()
    }

    override fun desktop(config: NativeLibrary.() -> Unit) {
        createDesktop().config()
    }

    override fun nativeDesktop() {
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
            componentRegistry.jvm()
            jvm = library
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
            val library = factory.newInstance(DefaultOperatingSystemTargetLibrary::class.java, operatingSystem, componentFactory)
            componentFactory.created(library)
            componentRegistry.forOperatingSystem(operatingSystem)
            library
        }
    }
}