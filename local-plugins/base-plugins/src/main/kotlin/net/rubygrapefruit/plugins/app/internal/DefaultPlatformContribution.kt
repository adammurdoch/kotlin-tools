package net.rubygrapefruit.plugins.app.internal

class DefaultPlatformContribution(override val main: HasDependencies, override val test: HasDependencies): PlatformContribution, HasTests
