# CLI argument parser

A lightweight CLI argument parser for Kotlin, targeting JVM 11+, macOS, Linux and Windows.

This library has no dependencies and can be used stand-alone.
However, it is intended to be used with the [cli-app](../cli-app) library, which adds more capabilities to help implement a CLI application using Kotlin.

Features:

- Options
  - `String`, `Boolean` and `Integer` typed arguments.
  - Choose from a set of value
  - Custom type argument
- Positional parameters
    - `String`, `Boolean` and `Integer` typed arguments.
    - Choose from a set of value
    - Custom type argument
    - Optional, required and multi-value parameters. 
- Actions
    - Arbitrary nesting of actions.
    - Use options or positional parameters, or a mix.
