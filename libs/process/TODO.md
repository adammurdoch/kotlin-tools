
## TODO

- Windows fork process:
  - `CreateProcess()`, returns handles to stdout/err/in, can pass `STARTUPINFO` structure to provide stdout/err/in handles
  - `WaitForSingleObject()`
  - `GetExitCodeProcess()`
  - An example with redirected io: https://learn.microsoft.com/en-us/windows/win32/ProcThread/creating-a-child-process-with-redirected-input-and-output 
- Mutex in parent process around pipe setup, to avoid inheritance of descriptors/handles
- Fork process with a TTY
