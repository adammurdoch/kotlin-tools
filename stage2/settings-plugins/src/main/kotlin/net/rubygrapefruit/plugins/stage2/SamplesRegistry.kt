package net.rubygrapefruit.plugins.stage2

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