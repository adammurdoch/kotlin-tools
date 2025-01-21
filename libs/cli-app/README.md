# Kotlin tools CLI application library

A small framework to help implement a CLI application using Kotlin multiplatform.

Targets:
- JVM 11+
- Browser
- MacOS x64 and arm64
- Windows x64
- Linux x64

Extends the [cli-args](../cli-args) library and adds support for:

- Command line argument parsing
- Help messages
- ZSH command-line completion
- Process exit code on success or failure
- Show or hide stack traces on failure
- File and directory parameters
