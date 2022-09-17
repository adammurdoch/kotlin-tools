package net.rubygrapefruit.app.plugins

import net.rubygrapefruit.app.internal.DefaultJvmApplication
import net.rubygrapefruit.app.internal.JvmModuleRegistry
import net.rubygrapefruit.app.internal.applications
import net.rubygrapefruit.app.internal.toModuleName
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

            applications.withApp<DefaultJvmApplication> { app ->
                app.module.name.convention(app.appName.map(::toModuleName))

                val jarTask = tasks.named("jar", Jar::class.java)
                val runtimeClasspath = configurations.getByName("runtimeClasspath")

                val moduleInfoCp = extensions.getByType(JvmModuleRegistry::class.java).moduleInfoClasspathEntryFor(app.module, null, null, runtimeClasspath)

                val sourceSet = extensions.getByType(SourceSetContainer::class.java).getByName("main")
                sourceSet.output.dir(moduleInfoCp)

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