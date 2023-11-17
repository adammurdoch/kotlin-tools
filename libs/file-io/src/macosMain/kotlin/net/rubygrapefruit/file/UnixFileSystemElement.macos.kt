package net.rubygrapefruit.file

import platform.posix.stat

internal actual fun lastModified(stat: stat): Timestamp {
    return Timestamp.of(stat.st_ctimespec.tv_sec, stat.st_ctimespec.tv_nsec)
}