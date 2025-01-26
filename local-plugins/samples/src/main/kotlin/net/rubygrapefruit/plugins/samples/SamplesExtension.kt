package net.rubygrapefruit.plugins.samples

import org.gradle.api.file.DirectoryProperty

interface SamplesExtension {
    val samplesDirectory: DirectoryProperty
}