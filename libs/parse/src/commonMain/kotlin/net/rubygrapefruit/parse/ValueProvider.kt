package net.rubygrapefruit.parse

internal interface ValueProvider<out OUT> {
    fun get(): OUT

    companion object {
        val Nothing = of(Unit)

        fun <OUT> of(value: OUT): ValueProvider<OUT> {
            return object : ValueProvider<OUT> {
                override fun get(): OUT {
                    return value
                }
            }
        }
    }
}