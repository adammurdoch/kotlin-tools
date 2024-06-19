package net.rubygrapefruit.cli

internal class DefaultStringParameter(
    name: String,
    help: String?,
    default: String?,
    host: Host,
    owner: Action
) : DefaultParameter<String>(name, help, default, host, owner, NoOpConverter), StringParameter {
    override fun int(): Parameter<Int> {
        return owner.replace(this, DefaultParameter(name, help, null, host, owner, IntConverter))
    }

    override fun path(): Parameter<FilePath> {
        return owner.replace(this, DefaultParameter(name, help, null, host, owner, FilePathConverter))
    }

    override fun <T : Any> oneOf(builder: Action.Choices<T>.() -> Unit): Parameter<T> {
        val choices = DefaultChoices<T>(host)
        builder(choices)
        return owner.replace(this, DefaultParameter(name, help, null, host, owner, ChoiceConverter(choices.choices)))
    }
}