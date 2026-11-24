package net.rubygrapefruit.plugins.stage2

class LibraryDependencies {
    val implementation = mutableListOf<Any>()

    fun implementation(notation: Any) {
        implementation.add(notation)
    }
}