package net.rubygrapefruit.plugins.app.internal

import org.gradle.api.file.Directory
import org.gradle.api.provider.SetProperty

/**
 * Generated resources that are included in a specific source set.
 */
interface HasGeneratedResources {
    val sourceSetName: String

    val generatedResources: SetProperty<Directory>
}