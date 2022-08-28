package net.rubygrapefruit.app.internal

import org.gradle.api.Project

internal val Project.applications
    get() = extensions.getByType(ApplicationRegistry::class.java)
