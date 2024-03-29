package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.Versions
import net.rubygrapefruit.plugins.app.internal.JvmModuleRegistry
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Provider
import org.gradle.jvm.toolchain.JavaLanguageVersion

class JvmConventionsPlugin : Plugin<Project> {
    companion object {
        fun javaVersion(project: Project, version: Int) {
            project.run {
                plugins.withId("java") {
                    val java = extensions.getByType(JavaPluginExtension::class.java)
                    java.toolchain.languageVersion.set(JavaLanguageVersion.of(version))
                }
            }
        }

        fun javaVersion(project: Project, version: Provider<Int>) {
            project.run {
                plugins.withId("java") {
                    val java = extensions.getByType(JavaPluginExtension::class.java)
                    java.toolchain.languageVersion.set(version.map { JavaLanguageVersion.of(it) })
                }
            }
        }

        fun addApiConstraints(project: Project, apiConfiguration: String) {
            project.dependencies.constraints.add(apiConfiguration, "org.jetbrains:annotations") {
                it.version { it.require("16.0.3") }
                it.because("Automatic module name is not defined for earlier versions")
            }
        }
    }

    override fun apply(target: Project) {
        with(target) {
            javaVersion(this, Versions.java)
            extensions.create("jvmModules", JvmModuleRegistry::class.java)
        }
    }
}