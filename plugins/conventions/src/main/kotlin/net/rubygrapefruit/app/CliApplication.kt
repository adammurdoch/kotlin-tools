package net.rubygrapefruit.app

import javax.inject.Inject

interface CliApplication {
    @get:Inject
    val distribution: Distribution
}