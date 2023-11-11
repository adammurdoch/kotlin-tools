package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.JvmLibrary
import net.rubygrapefruit.plugins.app.JvmModule
import net.rubygrapefruit.plugins.app.MultiPlatformLibrary
import net.rubygrapefruit.plugins.app.NativeMachine
import net.rubygrapefruit.plugins.bootstrap.Versions
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import javax.inject.Inject

internal open class DefaultMultiPlatformLibrary @Inject constructor(
    private val componentRegistry: MultiPlatformComponentRegistry,
    private val factory: ObjectFactory,
    private val projectName: String
) : MultiPlatformLibrary {
    private var jvm: JvmLibrary? = null

    val module: JvmModule
        get() = jvm!!.module

    override fun jvm() {
        jvm {}
    }

    override fun jvm(body: JvmLibrary.() -> Unit) {
        if (jvm == null) {
            val lib = factory.newInstance(JvmLibrary::class.java)
            lib.module.name.convention(toModuleName(projectName))
            lib.targetJavaVersion.convention(Versions.java)
            jvm = lib
        }
        body(jvm!!)
    }

    override fun nativeDesktop() {
        componentRegistry.registerSourceSets(
            ComponentTargets(
                targetJavaVersion(),
                setOf(
                    NativeMachine.LinuxX64,
                    NativeMachine.MacOSX64,
                    NativeMachine.MacOSArm64,
                    NativeMachine.WindowsX64
                )
            )
        )
    }

    private fun targetJavaVersion(): Provider<Int>? {
        return jvm?.targetJavaVersion
    }
}