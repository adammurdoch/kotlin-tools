package net.rubygrapefruit.file

import platform.posix.stat

internal actual fun lastModified(stat: stat): Timestamp {
    return Timestamp.of(stat.st_ctim.tv_sec, stat.st_ctim.tv_nsec)
}