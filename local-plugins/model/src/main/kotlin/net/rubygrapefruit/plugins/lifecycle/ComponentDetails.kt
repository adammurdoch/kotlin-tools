package net.rubygrapefruit.plugins.lifecycle

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
     * The target release coordinates.
     */
    val releaseCoordinates: Property<Coordinates>
}