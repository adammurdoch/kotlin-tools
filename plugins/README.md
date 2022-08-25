# Gradle Plugins

A collection of Gradle plugins for building applications from Kotlin.

- `net.rubygrapefruit.native-cli-app`
  - Builds a command-line application implemented in Kotlin/Native
  - Adds macOS and Linux targets.
  - Adds `nativeMain` and `nativeTest` source sets that are shared by these targets. You can add shared Kotlin/Native code
    to these source sets.

See ../test for some samples.

### TODO

- Add a settings plugin that add the maven repo and plugin dependencies to the root project.
