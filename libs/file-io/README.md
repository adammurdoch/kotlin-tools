# file-io

A Kotlin multiplatform library for using the file system.

The entry point is `FileSystem`

- Determine the type of a file system element.
- Files
  - Read and write ByteArrays
  - Read and write UTF-8 text
- Directories
  - Create a temporary directory
  - List contents of a directory
  - Create a directory
  - Delete a directory recursively
- Symlinks
  - Read and create a symlink
- Permissions
  - Read and write the POSIX permissions of a file system element.
