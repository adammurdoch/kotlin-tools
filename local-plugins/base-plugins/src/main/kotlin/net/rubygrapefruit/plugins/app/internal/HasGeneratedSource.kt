package net.rubygrapefruit.plugins.app.internal

import org.gradle.api.file.Directory
import org.gradle.api.provider.SetProperty

/**
 * Generated source that is included in a specific source set.
 */
interface HasGeneratedSource {
    val sourceSetName: String

    val generatedSource: SetProperty<Directory>
}