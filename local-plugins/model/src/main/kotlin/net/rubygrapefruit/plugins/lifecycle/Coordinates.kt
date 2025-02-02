package net.rubygrapefruit.plugins.lifecycle

import java.io.Serializable

@kotlinx.serialization.Serializable
data class Coordinates(
    val groupId: String,
    val artifactId: String,
    val version: String
) : Serializable {
    val formatted: String
        get() = "$groupId:$artifactId:$version"
}