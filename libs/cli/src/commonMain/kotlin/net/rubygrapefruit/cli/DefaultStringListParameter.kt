package net.rubygrapefruit.cli

internal class DefaultStringListParameter(
    name: String,
    help: String?,
    host: Host,
    owner: Action,
    default: List<String>,
    required: Boolean,
) : DefaultListParameter<String>(name, help, host, owner, default, required, NoOpConverter), StringListParameter {
    override fun path(): ListParameter<FilePath> {
        return owner.replace(this, DefaultListParameter(name, help, host, owner, emptyList(), required, FilePathConverter))
    }
}