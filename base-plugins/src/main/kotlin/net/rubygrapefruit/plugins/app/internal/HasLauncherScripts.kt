package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Distribution
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property

interface HasLauncherScripts : Distribution {
    /**
     * The bash script to copy into the distribution image.
     */
    val bashScript: RegularFileProperty

    /**
     * The Windows bat script to copy into the distribution image.
     */
    val batScript: RegularFileProperty

    /**
     * The location for the bash script in the distribution image.
     */
    val bashScriptPath: Property<String>

    /**
     * The location for the bat script in the distribution image.
     */
    val batScriptPath: Property<String>
}