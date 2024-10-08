## TODO

### Next

- Test queries and mutations after compaction
- Write-lock the files
- Thread safety

### Later

- Values as byte streams
- Serialize via streams using kotlinx-serialization-json-io
  - Replace calls to `Json.decodeFromString()` and `encodeToString()`
- Multi-map
- Reset generation to 1 at some point before overflowing to negative value
- Compact only once amount of garbage exceeds certain threshold
- Avoid copying large values on compaction
- Don't create a new buffer for each block copied during compaction
- Buffered reads and writes
- Background compaction
- Don't keep checking the current position when reading index changes from file
  - Use a counter for the expected number of changes
- Reuse encoder and decoder instances
- Cache the key -> encoded key mapping in memory for key-value store
- Use the key hash in key-value store index
  - md5 -> 128 bits 16 bytes 2 longs
  - sha1 -> 160 bits, 20 bytes 2.5 longs
  - sha2 -> 224, 256, 384, 512 bits, 28, 32, 48, 64 bytes
- Don't keep entire index in memory
- Tests verify file contents
- Test app dumps store contents
  - Value size
- Protect from partially written files
- Test app benchmarks read and write - one value, many values, multiple threads
- Multi-thread safety: threads that read/write separate values, threads read/write same values
  - Prevent concurrent updates of index
- Multi-process safety
  - Write-lock the files
  - Collaborative locking between processes
- Atomic update
- Iterator
- Background update
  - e.g. register a mutation for a given value and apply it before value is next read
  - e.g. register a mutation for all values and apply before each is read
- Concurrent reads
- Optimistic concurrent updates
- Encoding uses type tags?
- Encoding uses variable length encoding for string length
- Use variable length encoding for block size, store id, etc
- Use a more compact encoding than JSON, and stream to file rather than collecting in a string
- Async write to file
- Option for storing secrets, eg set permissions, encode
- Optionally discard on incompatible version
- fsync for durability
