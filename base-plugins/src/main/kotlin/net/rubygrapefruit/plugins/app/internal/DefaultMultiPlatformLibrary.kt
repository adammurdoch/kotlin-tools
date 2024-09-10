package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.JvmLibrary
import net.rubygrapefruit.plugins.app.JvmModule
import net.rubygrapefruit.plugins.app.LibraryDependencies
import net.rubygrapefruit.plugins.app.MultiPlatformLibrary
import net.rubygrapefruit.plugins.app.NativeLibrary
import net.rubygrapefruit.plugins.app.Versions
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

internal open class DefaultMultiPlatformLibrary @Inject constructor(
    private val componentRegistry: MultiPlatformComponentRegistry,
    private val factory: ObjectFactory,
    private val project: Project
) : MultiPlatformLibrary {
    private var jvm: JvmLibrary? = null
    private var macOs: NativeLibrary? = null

    val module: JvmModule
        get() = jvm!!.module

    override fun jvm() {
        jvm {}
    }

    override fun jvm(config: JvmLibrary.() -> Unit) {
        if (jvm == null) {
            val lib = factory.newInstance(DefaultJvmLibrary::class.java, "jvmMain")
            lib.module.name.convention(toModuleName(project.name))
            lib.targetJavaVersion.convention(Versions.java)
            jvm = lib
            componentRegistry.jvm(lib.targetJavaVersion)
        }
        config(jvm!!)
    }

    override fun browser() {
        componentRegistry.browser()
    }

    override fun macOS() {
        componentRegistry.macOS()
    }

    override fun macOS(config: NativeLibrary.() -> Unit) {
        componentRegistry.macOS()
        if (macOs == null) {
            macOs = factory.newInstance(DefaultNativeLibrary::class.java, "macosMain")
        }
        config(macOs!!)
    }

    override fun nativeDesktop() {
        componentRegistry.nativeDesktop()
    }

    override fun common(config: LibraryDependencies.() -> Unit) {
        project.kotlin.sourceSets.getByName("commonMain").dependencies { config(KotlinHandlerBackedLibraryDependencies(this)) }
    }

    override fun test(config: LibraryDependencies.() -> Unit) {
        project.kotlin.sourceSets.getByName("commonTest").dependencies { config(KotlinHandlerBackedLibraryDependencies(this)) }
    }
}