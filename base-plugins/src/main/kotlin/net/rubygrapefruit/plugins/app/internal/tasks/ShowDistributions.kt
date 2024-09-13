package net.rubygrapefruit.plugins.app.internal.tasks

import net.rubygrapefruit.plugins.app.Application
import net.rubygrapefruit.plugins.app.Distribution
import net.rubygrapefruit.plugins.app.internal.*
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

        val comparator = Comparator.comparingInt<MutableDistribution> {
            when {
                it == defaultDist -> 1
                it.canBuildOnHostMachine -> 2
                else -> 3
            }
        }.thenBy { dist -> dist.name }

        val distributions = app.distributions.get().filterIsInstance<MutableDistribution>().sortedWith(comparator)
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
            println("Dist task: ${distribution.distTask.get().path}")

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