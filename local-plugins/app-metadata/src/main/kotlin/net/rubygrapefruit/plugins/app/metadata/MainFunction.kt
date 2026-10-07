package net.rubygrapefruit.plugins.app.metadata

import kotlinx.serialization.Serializable

@Serializable
data class MainFunction(val path: String, val ownerJvmClass: String, val packageName: String, val hasParam: Boolean)