package net.rubygrapefruit.file

import java.nio.file.attribute.PosixFilePermission

internal fun Set<PosixFilePermission>.permissions(): PosixPermissions {
    var mode = 0u
    if (contains(PosixFilePermission.OWNER_READ)) {
        mode = mode.or(PosixPermissions.ownerRead)
    }
    if (contains(PosixFilePermission.OWNER_WRITE)) {
        mode = mode.or(PosixPermissions.ownerWrite)
    }
    if (contains(PosixFilePermission.OWNER_EXECUTE)) {
        mode = mode.or(PosixPermissions.ownerExecute)
    }
    if (contains(PosixFilePermission.GROUP_READ)) {
        mode = mode.or(PosixPermissions.groupRead)
    }
    if (contains(PosixFilePermission.GROUP_WRITE)) {
        mode = mode.or(PosixPermissions.groupWrite)
    }
    if (contains(PosixFilePermission.GROUP_EXECUTE)) {
        mode = mode.or(PosixPermissions.groupExecute)
    }
    if (contains(PosixFilePermission.OTHERS_READ)) {
        mode = mode.or(PosixPermissions.othersRead)
    }
    if (contains(PosixFilePermission.OTHERS_WRITE)) {
        mode = mode.or(PosixPermissions.othersWrite)
    }
    if (contains(PosixFilePermission.OTHERS_EXECUTE)) {
        mode = mode.or(PosixPermissions.othersExecute)
    }
    return PosixPermissions(mode)
}

internal fun PosixPermissions.permSet(): Set<PosixFilePermission> {
    val result = mutableSetOf<PosixFilePermission>()
    if (mode and PosixPermissions.ownerRead != 0u) {
        result.add(PosixFilePermission.OWNER_READ)
    }
    if (mode and PosixPermissions.ownerWrite != 0u) {
        result.add(PosixFilePermission.OWNER_WRITE)
    }
    if (mode and PosixPermissions.ownerExecute != 0u) {
        result.add(PosixFilePermission.OWNER_EXECUTE)
    }
    if (mode and PosixPermissions.groupRead != 0u) {
        result.add(PosixFilePermission.GROUP_READ)
    }
    if (mode and PosixPermissions.groupWrite != 0u) {
        result.add(PosixFilePermission.GROUP_WRITE)
    }
    if (mode and PosixPermissions.groupExecute != 0u) {
        result.add(PosixFilePermission.GROUP_EXECUTE)
    }
    if (mode and PosixPermissions.othersRead != 0u) {
        result.add(PosixFilePermission.OTHERS_READ)
    }
    if (mode and PosixPermissions.othersWrite != 0u) {
        result.add(PosixFilePermission.OTHERS_WRITE)
    }
    if (mode and PosixPermissions.othersExecute != 0u) {
        result.add(PosixFilePermission.OTHERS_EXECUTE)
    }
    return result
}