package net.rubygrapefruit.file

enum class FileSystemCapability {
    /**
     * Can posix permissions be set on a symlink (rather than the file the symlink points to)?
     */
    SetSymLinkPosixPermissions
}