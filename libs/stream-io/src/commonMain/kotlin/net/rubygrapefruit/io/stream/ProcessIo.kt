package net.rubygrapefruit.io.stream

import kotlinx.io.RawSink
import kotlinx.io.RawSource

/**
 * This process' output stream.
 */
expect val stdout: RawSink

/**
 * This process' error stream.
 */
expect val stderr: RawSink

/**
 * This process' input stream.
 */
expect val stdin: RawSource

internal class ThisProcess(override val displayName: String): StreamSource
