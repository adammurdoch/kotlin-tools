package net.rubygrapefruit.app

import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty

interface JvmModule {
    /**
     * The name of the module.
     */
    val name: Property<String>

    /**
     * The packages that this module exports.
     */
    val exports: SetProperty<String>

    /**
     * The modules that this module requires.
     */
    val requires: SetProperty<String>

    val requiresTransitive: SetProperty<String>
}