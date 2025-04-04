
## TODO

- Prevent `sink.close()` and `source.close()` in `write()`, `read()`, etc
- `Directory.listEntries()` queries metadata for each element in directory on JVM. Should make this lazy?
- `Directory.deleteRecursively()` uses an exception to signal that the directory does not exist.
- Strongly typed exceptions
  - `MissingDirectoryException`
- Use Kotlinx IO exceptions in source and sink implementations
- Windows: open files with `FILE_SHARE_READ` when reading?
- More efficient `RegularFile`
  - Each `FileContent` instance should reuse the same buffer?
  - `writeBytes()` copies byte array into buffer then to sink 
  - `writeText()` encodes into buffer then to sink
  - `readBytes()` could alloc byte array and read into it
- Seek to location
  - Represent locations using `ULong` 
- Lock file
  - `flock()`, `lockf()` or `fctl()`
  - `LockFileEx()` and `UnlockFile()`
- Query platform specific locations for applications
  - Directory for app to write data files to
  - Directory for app configuration
- Error handling for read and write on all platforms
- Error handling for read and write actions that fail
- Add some abstract permission functions, e.g. can read, set readable, etc
- Atomic `Directory.createTemporaryDirectory()` implementation on Windows
- Query symlinks on Windows as reparse points: FSCTL_GET_REPARSE_POINT
- Query Windows ACLs
- Query file systems on the machine
- Provide specialized subtypes of `FileSystemElement` for each platform
- Windows directory entry could carry metadata for the entry
- Atomic move
- Atomic recursive delete, if supported by FS
- Remove a symlink
- Canonicalize an element: `realpath()` and `GetFinalPathNameByHandleW()`
- Calculate relative paths
- Add context exceptions to all operations
- Test delete files, directories and symlinks
- Test set/get permissions on symlinks and directories
- Test visiting tree top down
- Test failure modes for reading/writing text
- Test failure modes for reading/writing bytes
- Test failure modes for reading/writing symlinks
- Test failure modes for listing directory contents (exists and is not a directory, etc)
- Test failure modes for get/set permissions
- Test failure modes for visiting tree
- Test read/write text or list entries via a broken symlink
- Test unicode directory names
- Test unicode symlink names and contents
- Test name resolution
- Test case-insensitive lookup
