package net.rubygrapefruit.plugins.bootstrap

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

class IncludedBuildPlugin : Plugin<Settings> {
    override fun apply(target: Settings) {
        target.run {
            gradle.rootProject { project ->
                with(project) {
                    plugins.apply("lifecycle-base")
                    val includedBuilds = target.gradle.includedBuilds
                    tasks.named("clean") {
                        it.dependsOn(includedBuilds.map { it.task(":clean") })
                        it.dependsOn(subprojects.map { it.tasks.findByName("clean") })
                    }
                    tasks.named("check") {
                        it.dependsOn(includedBuilds.map { it.task(":check") })
                        it.dependsOn(subprojects.map { it.tasks.findByName("check") })
                    }
                    tasks.named("assemble") {
                        it.dependsOn(includedBuilds.map { it.task(":assemble") })
                        it.dependsOn(subprojects.map { it.tasks.findByName("assemble") })
                    }
                    tasks.register("dist") {
                        it.dependsOn(includedBuilds.map { it.task(":dist") })
                        it.dependsOn(subprojects.mapNotNull { it.tasks.findByName("dist") })
                    }
                }
            }
        }
    }
}