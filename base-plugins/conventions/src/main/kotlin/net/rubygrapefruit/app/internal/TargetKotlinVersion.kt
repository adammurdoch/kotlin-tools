package net.rubygrapefruit.app.internal

import net.rubygrapefruit.app.plugins.KotlinBasePlugin

internal val targetKotlinVersion by lazy {
    KotlinBasePlugin::class.java.getResource("/kotlin-version.txt")!!.readText()
}