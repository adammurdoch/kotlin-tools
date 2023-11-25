@file:OptIn(ExperimentalForeignApi::class)

package net.rubygrapefruit.file

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.convert
import platform.posix.stat

internal actual fun lastModified(stat: stat): Timestamp {
    return Timestamp.of(stat.st_ctim.tv_sec, stat.st_ctim.tv_nsec)
}

internal actual fun mode(stat: stat): UInt {
    return stat.st_mode.convert()
}

actual val canSetSymLinkPermissions: Boolean
    get() = false
