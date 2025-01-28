package net.rubygrapefruit.plugins.lifecycle

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.Property

interface ComponentDetails {
    val description: Property<String>

    /**
     * The next version of this component to release.
     */
    val nextVersion: Property<String>

    /**
     * The version of this component to build.
     */
    val targetVersion: Property<VersionNumber>

    /**
     * The coordinates of the version being built.
     */
    val targetCoordinates: Property<Coordinates>

    /**
     * The coordinates of the most recent release.
     */
    val releaseCoordinates: Property<Coordinates>

    val repository: ConfigurableFileCollection
}