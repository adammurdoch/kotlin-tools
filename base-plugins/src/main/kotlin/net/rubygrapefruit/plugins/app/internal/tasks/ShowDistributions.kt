package net.rubygrapefruit.plugins.app.internal.tasks

import net.rubygrapefruit.plugins.app.Application
import net.rubygrapefruit.plugins.app.Distribution
import net.rubygrapefruit.plugins.app.internal.DefaultDistributionWithImage
import net.rubygrapefruit.plugins.app.internal.HasEmbeddedJvm
import net.rubygrapefruit.plugins.app.internal.HasLauncherExecutable
import net.rubygrapefruit.plugins.app.internal.HasLauncherScripts
import net.rubygrapefruit.plugins.app.internal.HasTargetMachine
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

abstract class ShowDistributions : DefaultTask() {
    @get:Internal
    abstract val app: Property<Application>

    @TaskAction
    fun report() {
        val app = app.get()
        val defaultDist = app.distribution.orNull

        println("Application: ${app.appName.get()}")
        println()

        val distributions = app.distributions.get().filterIsInstance<DefaultDistributionWithImage>().sortedBy {
            when {
                it == defaultDist -> 1
                it.canBuildOnHostMachine -> 2
                else -> 3
            }
        }
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
            if (distribution is HasTargetMachine) {
                println("Target machine: ${distribution.targetMachine}")
            }
            if (distribution is HasLauncherExecutable) {
                println("Build type: ${distribution.buildType}")
            }
            println("Launcher: ${launcherFor(distribution)}")
            if (distribution is HasEmbeddedJvm) {
                println("Embedded JVM: yes")
            }
            println("Dist task: ${distribution.distTask.name}")

            val imageDirectory = distribution.imageOutputDirectory.get()
            val launcher = imageDirectory.file(distribution.effectiveLauncherFilePath.get())

            println("Image dir: $imageDirectory")
            println("Launcher path: $launcher")
        }
    }

    private fun launcherFor(distribution: Distribution): String {
        return when (distribution) {
            is HasLauncherScripts -> "Scripts"
            is HasLauncherExecutable -> "Executable"
            else -> "No launcher"
        }
    }
}