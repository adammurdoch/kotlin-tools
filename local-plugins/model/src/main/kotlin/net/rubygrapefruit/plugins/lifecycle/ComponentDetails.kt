package net.rubygrapefruit.plugins.lifecycle

import org.gradle.api.provider.Property

interface ComponentDetails {
    val description: Property<String>

    val nextVersion: Property<String>

    val releaseVersion: Property<String>

    val releaseCoordinates: Property<Coordinates>
}