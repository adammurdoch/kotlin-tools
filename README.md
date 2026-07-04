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

- [`local-plugins`](local-plugins/) contains some Gradle plugins
  - Also contains some Kotlin libraries that are used by the Gradle plugins.
  - Some of these plugins are also used to build the Gradle plugins and libraries, via `stage3` below. 
- [`libs`](libs/) contains some Kotlin libraries.
- [`test-apps`](test-apps/) contains some sample applications and libraries that use the Gradle plugins.
- [`stage0`](stage0/) contains Gradle plugins that can build plugins implemented using Java. They are also implemented using Java
- [`stage1`](stage1/) contains Gradle plugins that can build plugins implemented using Kotlin. They are implemented using Java.
- [`stage2`](stage2/) contains Gradle plugins that can build and release libraries and Gradle plugins implemented using Kotlin.
- [`stage3`](stage3/) wraps libraries and plugins from `local-plugins` so that they can be used to build themselves.

The file [`versions.toml`](versions.toml) defines the versions of all the dependencies of the build, the plugins and libraries and conventions for libraries and applications built by these plugins.