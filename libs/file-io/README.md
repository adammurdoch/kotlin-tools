# file-io

A Kotlin multi-platform library for accessing the file system.

The entry point is `FileSystem`

- Determine the type of an element in the file system
- Read and write UTF-8 text to a file
- List contents of a directory
- Create a directory
- Read and write a symlink

## TODO 

- Add context exceptions to all operations
- Test failure modes for reading text
- Test failure modes for reading/writing symlinks
- Test failure modes for listing directory contents (exists and is not a directory, etc)
- Test read/write text or list entries via a broken symlink
- Test unicode directory names
- Test unicode symlink names and contents
- Test name resolution
- Test case insensitive lookup
