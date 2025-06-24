package net.rubygrapefruit.plugins.app.internal.tasks

import net.rubygrapefruit.plugins.app.Distribution
import net.rubygrapefruit.plugins.app.internal.ApplicationMetadata
import net.rubygrapefruit.plugins.app.internal.DistributionMetadata
import net.rubygrapefruit.plugins.app.internal.HasLauncherExecutable
import net.rubygrapefruit.plugins.app.internal.HasLauncherScripts
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

internal abstract class ShowApplication : DefaultTask() {
    @get:Internal
    abstract val app: Property<ApplicationMetadata>

    @TaskAction
    fun report() {
        val app = app.get()
        val defaultDist = app.distribution

        println("Application: ${app.appName}")
        println()

        val comparator = Comparator.comparingInt<DistributionMetadata> {
            when {
                it == defaultDist -> 1
                it.canBuildOnHostMachine -> 2
                else -> 3
            }
        }.thenBy { dist -> dist.name }

        val distributions = app.distributions.sortedWith(comparator)
        println("Distributions:")
        for (distribution in distributions) {
            println()
            print("Name: ${distribution.name}")
            if (!(distribution.canBuildOnHostMachine)) {
                print(" (not buildable)")
            }
            if (distribution == defaultDist) {
                print(" (DEFAULT)")
            }
            println()
            if (distribution.targetMachine != null) {
                println("Target machine: ${distribution.targetMachine}")
            }
            if (distribution.buildType != null) {
                println("Build type: ${distribution.buildType}")
            }
            println("Launcher: ${distribution.launcherType}")
            if (distribution.hasEmbeddedJvm) {
                println("Embedded JVM: yes")
            }
            println("Dist task: ${distribution.distTask}")

            println("Image dir: ${distribution.imageDirectory}")
            println("Launcher path: ${distribution.launcher}")
        }

        println()
        println("Installations:")
        for (installation in app.installations) {
            println()
            println("Image dir: ${installation.imageDirectory}")
            println("Launcher path: ${installation.launcher}")
        }
    }
}