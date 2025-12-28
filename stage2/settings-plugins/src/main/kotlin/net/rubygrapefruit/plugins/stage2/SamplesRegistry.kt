package net.rubygrapefruit.plugins.stage2

import org.gradle.api.Project
import org.gradle.api.Task
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

        for (sample in samples) {
            rootProject.project(":${sample.name}") { project ->
                project.tasks.register("verifySample") { task ->
                    when (sample) {
                        is Lib -> sample.verifyLib(task)
                        is CliApp -> sample.verifyCliApp(task)
                        is UiApp -> sample.verifyUiApp(task)
                    }
                }
            }
        }

        val tasks = samples.map { ":${it.name}:verifySample" }

        rootProject.tasks.register("verifySample") { task ->
            task.dependsOn(tasks)
        }
    }
}

private fun CliApp.verifyCliApp(task: Task) {
    task.dependsOn(":$name:${distribution.distTask}")
    task.doLast {
        println("CLI app: $name")
    }
}

private fun Lib.verifyLib(task: Task) {
    task.dependsOn(":$name:build")
    task.doLast {
        println("Lib: $name")
    }
}

private fun UiApp.verifyUiApp(task: Task) {
    task.dependsOn(":$name:${distribution.distTask}")
    task.doLast {
        println("UI app: $name")
    }
}