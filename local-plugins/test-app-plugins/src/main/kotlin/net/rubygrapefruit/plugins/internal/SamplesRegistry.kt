package net.rubygrapefruit.plugins.internal

import org.gradle.api.Project
import org.gradle.api.initialization.Settings

abstract class SamplesRegistry(private val settings: Settings) : SampleContainer {
    private val samples = mutableListOf<Sample>()

    fun jvmLib(name: String, config: JvmLibBuilder.() -> Unit = {}): JvmLib {
        val builder = JvmLibBuilder(name, this)
        builder.config()
        return builder.register()
    }

    fun kmpLib(name: String, config: KmpLibBuilder.() -> Unit = {}): KmpLib {
        val builder = KmpLibBuilder(name, this)
        builder.config()
        return builder.register()
    }

    fun jvmCliApp(name: String, config: JvmCliAppBuilder.() -> Unit = {}): JvmCliApp {
        val builder = JvmCliAppBuilder(name, this)
        builder.config()
        return builder.register()
    }

    fun nativeCliApp(name: String, config: NativeCliAppBuilder.() -> Unit = {}): NativeCliApp {
        val builder = NativeCliAppBuilder(name, this)
        builder.config()
        return builder.register()
    }

    fun jvmUiApp(name: String, config: JvmUiAppBuilder.() -> Unit = {}): JvmUiApp {
        val builder = JvmUiAppBuilder(name, this)
        builder.config()
        return builder.register()
    }

    fun nativeUiApp(name: String, config: NativeUiAppBuilder.() -> Unit = {}): NativeUiApp {
        val builder = NativeUiAppBuilder(name, this)
        builder.config()
        return builder.register()
    }

    override fun <T : Sample> add(sample: T): T {
        samples.add(sample)
        settings.include(sample.name)
        return sample
    }

    internal fun applyTo(rootProject: Project) {
        for (f in settings.rootDir.listFiles()) {
            val ignore = listOf(".gradle", ".kotlin", "gradle", "kotlin-js-store", "build")
            if (f.isDirectory && !ignore.contains(f.name)) {
                if (samples.find { it.name == f.name } == null) {
                    throw IllegalStateException("Sample in $f is not declared")
                }
            }
        }

        val sampleTasks = samples.map { sample -> sample.verify(rootProject) }

        rootProject.tasks.register("verifySample") { task ->
            task.dependsOn(sampleTasks.map { it.verifyTaskName })
        }
        rootProject.tasks.register("verifyOtherDistributions") { task ->
            task.dependsOn(sampleTasks.flatMap { it.otherTaskNames })
        }
    }
}

private fun Sample.verify(rootProject: Project): SampleTasks {
    val project = rootProject.project(":${name}")
    return when (this) {
        is Lib -> verifyLib(project)
        is App -> verifyApp(project)
    }
}

private fun App.verifyApp(project: Project): SampleTasks {
    project.tasks.register("verifySample") { task ->
        task.dependsOn(distribution.distTask)
        task.doLast {
            verify(this, distribution)
        }
    }
    if (otherDistributions.isNotEmpty()) {
        project.tasks.register("verifyOtherDistributions") { task ->
            for (distribution in otherDistributions) {
                task.dependsOn(distribution.distTask)
                task.doLast {
                    verify(this, distribution)
                }
            }
        }
        return SampleTasks(":$name:verifySample", listOf(":$name:verifyOtherDistributions"))
    } else {
        return SampleTasks(":$name:verifySample", emptyList())
    }
}

private fun verify(app: App, distribution: AppDistribution) {
    when (app) {
        is CliApp -> println("CLI app: ${app.name} dist: ${distribution.distTask}")
        is UiApp -> println("UI app: ${app.name} dist: ${distribution.distTask}")
    }
}

private fun Lib.verifyLib(project: Project): SampleTasks {
    project.tasks.register("verifySample") { task ->
        task.dependsOn("build")
        task.doLast {
            println("Lib: $name")
        }
    }
    return SampleTasks(":$name:verifySample", emptyList())
}

private class SampleTasks(val verifyTaskName: String, val otherTaskNames: List<String>)