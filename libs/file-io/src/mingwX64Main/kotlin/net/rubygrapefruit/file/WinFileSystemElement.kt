@file:OptIn(ExperimentalForeignApi::class)

package net.rubygrapefruit.file

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.memScoped
import platform.windows.CreateDirectoryW

internal open class WinFileSystemElement(override val path: WinPath) : AbstractFileSystemElement() {

    override val parent: Directory?
        get() = TODO("Not yet implemented")

    override fun metadata(): Result<ElementMetadata> {
        TODO("Not yet implemented")
    }

    override fun posixPermissions(): Result<PosixPermissions> {
        TODO("Not yet implemented")
    }

    override fun setPermissions(permissions: PosixPermissions) {
        TODO("Not yet implemented")
    }

    override fun supports(capability: FileSystemCapability): Boolean {
        TODO("Not yet implemented")
    }

    override fun snapshot(): Result<ElementSnapshot> {
        TODO("Not yet implemented")
    }

    override fun toFile(): RegularFile {
        return WinRegularFile(path)
    }

    override fun toDir(): Directory {
        return WinDirectory(path)
    }

    override fun toSymLink(): SymLink {
        return WinSymLink(path)
    }
}

internal class WinDirectory(path: WinPath) : WinFileSystemElement(path), Directory {

    override fun toDir(): Directory {
        return this
    }

    override fun file(name: String): RegularFile {
        return WinRegularFile(path.resolve(name))
    }

    override fun dir(name: String): Directory {
        return WinDirectory(path.resolve(name))
    }

    override fun symLink(name: String): SymLink {
        return WinSymLink(path.resolve(name))
    }

    override fun deleteRecursively() {
        TODO("Not yet implemented")
    }

    override fun createDirectories() {
        parent?.createDirectories()
        memScoped {
            if (CreateDirectoryW(absolutePath, null) != 0) {
                throw NativeException("Could not create directory $absolutePath")
            }
        }
    }

    override fun createTemporaryDirectory(): Directory {
        TODO("Not yet implemented")
    }

    override fun listEntries(): Result<List<DirectoryEntry>> {
        TODO("Not yet implemented")
    }

    override fun visitTopDown(visitor: DirectoryEntry.() -> Unit) {
        TODO("Not yet implemented")
    }
}

internal class WinRegularFile(path: WinPath) : WinFileSystemElement(path), RegularFile {

    override fun toFile(): RegularFile {
        return this
    }

    override fun delete() {
        TODO("Not yet implemented")
    }

    override fun writeBytes(bytes: ByteArray) {
        TODO("Not yet implemented")
    }

    override fun readBytes(): Result<ByteArray> {
        TODO("Not yet implemented")
    }

    override fun writeText(text: String) {
        TODO("Not yet implemented")
    }

    override fun readText(): Result<String> {
        TODO("Not yet implemented")
    }
}

internal class WinSymLink(path: WinPath) : WinFileSystemElement(path), SymLink {

    override fun toSymLink(): SymLink {
        return this
    }

    override fun readSymLink(): Result<String> {
        TODO("Not yet implemented")
    }

    override fun writeSymLink(target: String) {
        TODO("Not yet implemented")
    }
}