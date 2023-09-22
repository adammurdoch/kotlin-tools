package net.rubygrapefruit.app

import net.rubygrapefruit.plugins.bootstrap.Versions

/**
 * Various target versions for the plugins.
 */
abstract class Versions {
    val kotlin: String
        get() = Versions.kotlin

    val java: Int
        get() = Versions.java

    val pluginsJava: Int
        get() = Versions.pluginsJava
}