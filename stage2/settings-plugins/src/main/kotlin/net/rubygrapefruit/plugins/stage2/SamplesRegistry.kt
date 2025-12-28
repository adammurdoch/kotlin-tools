package net.rubygrapefruit.plugins.stage2

import org.gradle.api.initialization.Settings

abstract class SamplesRegistry(private val settings: Settings) : SampleContainer {
    private val samples = mutableListOf<Sample>()

    fun jvmLib(name: String): JvmLib {
        return add(JvmLib(name, this))
    }

    fun kmpLib(name: String): KmpLib {
        return add(KmpLib(name, this))
    }

    fun jvmCliApp(name: String, config: JvmCliAppBuilder.() -> Unit = {}): JvmCliApp {
        return add(JvmCliApp(name, this))
    }

    fun nativeCliApp(name: String, config: NativeCliAppBuilder.() -> Unit = {}): NativeCliApp {
        return add(NativeCliApp(name, this))
    }

    fun jvmUiApp(name: String): JvmUiApp {
        return add(JvmUiApp(name, this))
    }

    fun nativeUiApp(name: String): NativeUiApp {
        return add(NativeUiApp(name, this))
    }

    override fun <T : Sample> add(sample: T): T {
        samples.add(sample)
        settings.include(sample.name)
        return sample
    }

    internal fun validate() {
        for (f in settings.rootDir.listFiles()) {
            val ignore = listOf(".gradle", ".kotlin", "gradle", "kotlin-js-store", "build")
            if (f.isDirectory && !ignore.contains(f.name)) {
                if (samples.find { it.name == f.name } == null) {
                    throw IllegalStateException("Sample $f is not declared")
                }
            }
        }
    }
}