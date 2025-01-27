package net.rubygrapefruit.plugins.lifecycle

import org.gradle.api.provider.Property

interface ComponentDetails {
    val releaseCoordinates: Property<Coordinates>
}