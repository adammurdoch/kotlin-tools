package net.rubygrapefruit.plugins.app.internal.tasks

import kotlinx.serialization.Serializable

@Serializable
class InferredModule(
    val name: String,
    val fileName: String,
    val automatic: Boolean,
    val requires: List<String>
)

@Serializable
class Modules(
    val modules: List<InferredModule>,
    val requires: List<String>,
    val transitive: List<String>
)