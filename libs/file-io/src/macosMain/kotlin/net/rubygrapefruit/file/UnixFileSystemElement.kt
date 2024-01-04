@file:OptIn(ExperimentalForeignApi::class)

package net.rubygrapefruit.file

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.convert
import platform.posix.mode_t
import platform.posix.open
import platform.posix.stat

internal actual fun lastModified(stat: stat): Timestamp {
    return Timestamp.of(stat.st_ctimespec.tv_sec, stat.st_ctimespec.tv_nsec)
}

internal actual fun mode(stat: stat): UInt {
    return stat.st_mode.convert()
}

internal actual fun doOpen(path: String, flags: Int, mode: UInt): Int {
    return open(path, flags, mode.convert<mode_t>())
}

actual val canSetSymLinkPermissions: Boolean
    get() = true
