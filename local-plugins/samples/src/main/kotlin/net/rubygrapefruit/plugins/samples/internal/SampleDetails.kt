package net.rubygrapefruit.plugins.samples.internal

import kotlinx.serialization.Serializable

@Serializable
data class SampleDetails(val dir: String, val name: String)

