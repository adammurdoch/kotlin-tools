# Kotlin tools

Kotlin tools is a collection of useful tools for developing Kotlin applications and libraries.

## Gradle Plugins

Kotlin tools includes several [Gradle plugins](local-plugins/base-plugins/) to help simplify building applications and libraries using Kotlin, including Kotlin multiplatform.

- Build CLI applications implemented using Kotlin/JVM, optionally building native executables.
- Build CLI applications implemented using Kotlin native.
- Build UI applications implemented using Kotlin.
- Build libraries implemented using Kotlin.

## Libraries

Kotlin tools includes several Kotlin multiplatform libraries:

- [`stream-io`](libs/stream-io/): Extends [Kotlinx IO](https://github.com/Kotlin/kotlinx-io) to add capabilities for streams, for example access to the process' stdin, stdout and stderr as a source or a sink. 
- [`file-io`](libs/file-io/): Extends [Kotlinx IO](https://github.com/Kotlin/kotlinx-io) to add further file system capabilities.
- [`file-fixtures`](libs/file-fixtures): Helps write tests that access the file system.
- [`process`](libs/process): Provides APIs to fork processes.
- [`cli-args`](libs/cli-args): A lightweight CLI argument parser.
- [`cli-app`](libs/cli-app): A small framework to simplify implementing CLI applications, including parsing CLI arguments.

## Repository layout

The source tree is arranged as follows:

- [`local-plugins`](local-plugins/) contains Gradle plugins that are used to build the other Gradle plugins. 
  - Also contains some Kotlin libraries that are used by the Gradle plugins.
- [`libs`](libs/) contains some Kotlin libraries.
- [`test-apps`](test-apps/) contains some sample applications and libraries that use the Gradle plugins.
