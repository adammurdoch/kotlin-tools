package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.Versions
import net.rubygrapefruit.plugins.app.internal.JvmModuleRegistry
import net.rubygrapefruit.plugins.app.internal.MutableJvmApplication
import net.rubygrapefruit.plugins.app.internal.applications
import net.rubygrapefruit.plugins.app.internal.tasks.RuntimeModulePath
import net.rubygrapefruit.plugins.app.internal.toModuleName
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class JvmApplicationBasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply("org.jetbrains.kotlin.jvm")
            plugins.apply(ApplicationBasePlugin::class.java)
            plugins.apply(JvmConventionsPlugin::class.java)

            applications.withApp<MutableJvmApplication> { app ->
                app.module.name.convention(app.appName.map(::toModuleName))

                app.targetJvmVersion.convention(Versions.java)
                JvmConventionsPlugin.javaVersion(this, app.targetJvmVersion)
                JvmConventionsPlugin.addApiConstraints(this, "implementation")

                val jarTask = tasks.named("jar", Jar::class.java)
                val runtimeClasspath = configurations.getByName("runtimeClasspath")

                val classesDir = files(tasks.named("compileKotlin", KotlinCompile::class.java).map { it.destinationDirectory })

                val moduleInfo = extensions.getByType(JvmModuleRegistry::class.java).inspectClassPathsFor(app.module, app, classesDir, null, runtimeClasspath)
                val moduleInfoCp = moduleInfo.moduleInfoClasspath

                val runtimeModulePath = tasks.register("runtimeModulePath", RuntimeModulePath::class.java) {
                    it.classpath.from(runtimeClasspath)
                    it.inferredModulesFile.set(moduleInfo.inferredModulesFile)
                    it.outputDirectory.set(layout.buildDirectory.dir("jvm/module-path"))
                }

                val sourceSet = extensions.getByType(SourceSetContainer::class.java).getByName("main")
                sourceSet.output.dir(moduleInfoCp)

                app.runtimeModulePath.from(jarTask.map { it.archiveFile })
                app.runtimeModulePath.from(runtimeModulePath.map { it.outputDirectory.asFileTree.matching { it.include("*.jar") } })

                JvmConventionsPlugin.parallelTests(this)
            }
        }
    }
}