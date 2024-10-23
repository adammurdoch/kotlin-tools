# file-io

A Kotlin multiplatform library for accessing the file system. Supports JVM, macOS, Linux and Windows.

The entry point is `FileSystem`

- Files
  - Read and write byte streams
  - Read and write `ByteArray`
  - Read and write UTF-8 text
  - Seek to position in file for read or write
  - Query position in file
  - Delete file
- Directories
  - Create a temporary directory
  - List contents of a directory
  - Create a directory
  - Delete a directory recursively
- Symlinks
  - Read and create a symlink
  - Resolve a symlink to its target
- Query file system element type and other metadata.
- Permissions
  - Read and write the POSIX permissions of a file system element.
- Query the current directory
- Query the user's home directory
- Conversion
  - Convert from Kotlin IO `Path`
  - Convert from JVM `File` and `Path`
