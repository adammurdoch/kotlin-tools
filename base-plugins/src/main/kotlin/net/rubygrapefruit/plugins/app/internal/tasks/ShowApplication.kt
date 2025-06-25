package net.rubygrapefruit.plugins.app.internal.tasks

import net.rubygrapefruit.plugins.app.internal.ApplicationMetadata
import net.rubygrapefruit.plugins.app.internal.DistributionMetadata
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
        val devDist = app.devDistribution
        val releaseDist = app.releaseDistribution

        println("Application: ${app.appName}")
        println()

        val comparator = Comparator.comparingInt<DistributionMetadata> {
            when {
                it == devDist -> 1
                it == releaseDist -> 2
                it.canBuildOnHostMachine -> 3
                else -> 4
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
            if (distribution == devDist) {
                print(" (DEFAULT)")
            }
            if (distribution == releaseDist) {
                print(" (release)")
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