# file-io

A Kotlin multi-platform library for accessing the file system.

## TODO 

- Add context exceptions to all operations
- Add result object for readText(), so that it is consistent with metadata or dir contents query?
- Test failure modes for reading text
- Test failure modes for reading/writing symlinks
- Test failure modes for listing directory contents (exists and is not a directory, etc)
- Test read/write text or list entries via a broken symlink
- Test unicode directory names
- Test unicode symlink names and contents
- Test name resolution
- Test case insensitive lookup
