package net.rubygrapefruit.plugins.app.internal.tasks

import kotlinx.serialization.Serializable

@Serializable
class InferredModule(
    val name: String,
    val automatic: Boolean
)

@Serializable
class Modules(
    val requires: List<InferredModule>,
    val transitive: List<InferredModule>
)