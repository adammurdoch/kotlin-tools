package net.rubygrapefruit.plugins.stage2

abstract class SamplesRegistry {
    fun samples(config: Builder.() -> Unit) {
        val builder = DefaultBuilder()
        builder.config()
    }

    interface Builder {
    }

    private class DefaultBuilder : Builder {
    }
}