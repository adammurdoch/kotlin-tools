# Kotlin tools CLI argument parser

A lightweight CLI argument parser for Kotlin multiplatform.

Targets:
- JVM 11+
- MacOS x64 and arm64
- Windows x64
- Linux x64

This library has no dependencies and can be used stand-alone.
However, it is intended to be used with the [cli-app](../cli-app) library, which adds more capabilities to simplify implementing a CLI application using Kotlin.

Features:

- Options
  - `String`, `Boolean` and `Integer` typed arguments.
  - Choose from a set of values
  - Custom type argument
  - Required, optional, multi-value options.
- Positional parameters
    - `String`, `Boolean` and `Integer` typed arguments.
    - Choose from a set of values
    - Custom type argument
    - Optional, required and multi-value parameters. 
- Actions
    - Arbitrary nesting of actions.
    - Define actions using options or positional parameters, or a mix.
