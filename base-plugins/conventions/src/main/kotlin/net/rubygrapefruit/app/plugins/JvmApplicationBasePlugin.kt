package net.rubygrapefruit.app.plugins

import net.rubygrapefruit.app.internal.JvmModuleRegistry
import net.rubygrapefruit.app.internal.MutableJvmApplication
import net.rubygrapefruit.app.internal.applications
import net.rubygrapefruit.app.internal.toModuleName
import net.rubygrapefruit.plugins.bootstrap.Versions
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.jvm.tasks.Jar

class JvmApplicationBasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply("org.jetbrains.kotlin.jvm")
            plugins.apply(ApplicationBasePlugin::class.java)
            plugins.apply(JvmConventionsPlugin::class.java)

            applications.withApp<MutableJvmApplication> { app ->
                app.module.name.convention(app.appName.map(::toModuleName))

                app.targetJavaVersion.convention(Versions.java)
                JvmConventionsPlugin.javaVersion(this, app.targetJavaVersion)

                val jarTask = tasks.named("jar", Jar::class.java)
                val runtimeClasspath = configurations.getByName("runtimeClasspath")

                val moduleInfoCp = extensions.getByType(JvmModuleRegistry::class.java).moduleInfoClasspathEntryFor(app.module, null, null, runtimeClasspath)

                val sourceSet = extensions.getByType(SourceSetContainer::class.java).getByName("main")
                sourceSet.output.dir(moduleInfoCp)

                app.runtimeModulePath.from(jarTask.map { it.archiveFile })
                app.runtimeModulePath.from(runtimeClasspath)
            }
        }
    }
}