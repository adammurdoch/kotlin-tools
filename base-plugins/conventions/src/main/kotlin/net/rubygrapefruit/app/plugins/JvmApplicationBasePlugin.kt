package net.rubygrapefruit.app.plugins

import net.rubygrapefruit.app.internal.DefaultJvmApplication
import net.rubygrapefruit.app.internal.applications
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar

class JvmApplicationBasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply("java-library")
            plugins.apply("org.jetbrains.kotlin.jvm")
            plugins.apply(ApplicationBasePlugin::class.java)
            applications.withApp<DefaultJvmApplication> { app ->
                app.module.convention(app.appName)

                val jarTask = tasks.named("jar", Jar::class.java)
                val runtimeClasspath = configurations.getByName("runtimeClasspath")

                app.outputModulePath.from(jarTask.map { it.archiveFile })
                app.outputModulePath.from(runtimeClasspath)
                app.outputModuleNames.add(jarTask.flatMap { it.archiveFileName })
                val libNames = runtimeClasspath.elements.map { it.map { f -> f.asFile.name } }
                app.outputModuleNames.addAll(libNames)

                app.distribution.modulePath.from(app.outputModulePath)
                app.distribution.modulePathNames.convention(app.outputModuleNames)
            }
        }
    }
}