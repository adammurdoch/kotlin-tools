package net.rubygrapefruit.parse.text

interface TextFailureContext {
    val position: CharPosition

    val lineText: String
}