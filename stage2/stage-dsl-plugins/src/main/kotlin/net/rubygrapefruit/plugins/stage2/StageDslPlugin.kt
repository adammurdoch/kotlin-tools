package net.rubygrapefruit.plugins.stage2

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

@Suppress("unused")
abstract class StageDslPlugin : Plugin<Settings> {
    override fun apply(target: Settings) {
        target.run {
            val projects = extensions.create("projects", ProjectBuilder::class.java, target)
            gradle.rootProject { rootProject ->
                rootProject.tasks.register("generate") { task ->
                    val specs = projects.projects
                    task.doLast {
                        for (spec in specs.get()) {
                            val sourceBuildScript = spec.sourceProjectDir.resolve("build.gradle.kts")
                            val buildScript = spec.projectDir.resolve("build.gradle.kts")
                            buildScript.parentFile.mkdirs()
                            val scriptContent = sourceBuildScript.readText()
                            val modified = scriptContent
                                .replace("id(\"net.rubygrapefruit.bootstrap.release\")", "id(\"net.rubygrapefruit.stage2.release\")")
                                .replace("id(\"net.rubygrapefruit.bootstrap.samples\")", "/* id(\"net.rubygrapefruit.bootstrap.samples\") */")
                            buildScript.writeText(modified)
                        }
                    }
                }
            }
        }
    }
}