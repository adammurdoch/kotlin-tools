package net.rubygrapefruit.plugins.lifecycle

class Coordinates(
    val groupId: String,
    val artifactId: String,
    val version: String
) {
    val formatted: String
        get() = "$groupId:$artifactId:$version"
}