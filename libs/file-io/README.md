# file-io

A Kotlin multiplatform library for using the file system.

The entry point is `FileSystem`

- Files
  - Read and write byte streams
  - Read and write `ByteArray`
  - Read and write UTF-8 text
  - Seek to position in file for read or write
- Directories
  - Create a temporary directory
  - List contents of a directory
  - Create a directory
  - Delete a directory recursively
- Symlinks
  - Read and create a symlink
- Permissions
  - Read and write the POSIX permissions of a file system element.
- Query the current directory
- Query the user's home directory
- Determine the type of a file system element.
