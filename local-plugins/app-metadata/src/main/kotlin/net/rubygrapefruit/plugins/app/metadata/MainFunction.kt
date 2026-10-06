package net.rubygrapefruit.plugins.app.metadata

import kotlinx.serialization.Serializable

@Serializable
data class MainFunction(val fileName: String, val takesArgs: Boolean)