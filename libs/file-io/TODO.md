
## TODO

- Add some abstract permission functions, eg can read, set readable, etc
- Atomic Directory.createTemporaryDirectory() implementation on Windows
- Query Windows ACLs
- Query file systems on the machine
- Provide specialized subtypes of FileSystemElement for each platform
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
- Windows get user home dir: https://docs.microsoft.com/en-us/windows/win32/api/userenv/nf-userenv-getuserprofiledirectorya
- Windows list directory: https://docs.microsoft.com/en-us/windows/win32/fileio/listing-the-files-in-a-directory
- Windows get file info: https://learn.microsoft.com/en-us/windows/win32/api/fileapi/nf-fileapi-getfileattributesexw
