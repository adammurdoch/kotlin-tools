package net.rubygrapefruit.file

enum class FileSystemCapability {
    /**
     * Can POSIX permission be queried or set?
     */
    PosixPermissions,
    /**
    * Can POSIX permissions be set on a symlink (rather than the file the symlink points to)?
     */
    SetSymLinkPosixPermissions
}