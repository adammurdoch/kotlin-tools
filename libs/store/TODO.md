## TODO

- Tests verify file contents
- Test app dumps store contents
- Keep data and index files open (e.g. use IO threads?)
- Keep index in memory
- Test app benchmarks read and write - one value, many values, multiple threads
- Store key-value pairs
- Multi-thread safety: threads that read/write separate values, threads read/write same values
- Multi-process safety
- Basic garbage collection
- Version the store format
  - Version the data file 
  - Include the encoding version in store metadata
- Atomic update
- Concurrent reads
- Optimistic concurrent updates
- Encoding uses type tags?
- Encoding uses variable length encoding for string length
- Use a more compact encoding
- Async write to file
- Option for storing secrets, eg set permissions, encode
- Option to discard on incompatible version

one-value --iterations 1000000: 15.66s user 81.66s system 99% cpu 1:38.04 total
many-values --iterations 200: 40.70s user 19.55s system 100% cpu 1:00.19 total
