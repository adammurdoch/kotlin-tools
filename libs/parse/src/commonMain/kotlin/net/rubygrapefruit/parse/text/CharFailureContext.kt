package net.rubygrapefruit.parse.text

interface CharFailureContext {
    val position: CharPosition

    val lineText: String
}