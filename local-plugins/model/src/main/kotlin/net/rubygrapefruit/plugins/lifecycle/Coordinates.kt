package net.rubygrapefruit.plugins.lifecycle

import java.io.Serializable

class Coordinates(
    val groupId: String,
    val artifactId: String,
    val version: String
): Serializable {
    val formatted: String
        get() = "$groupId:$artifactId:$version"
}