# Gradle Plugins

A collection of Gradle plugins for building applications using Kotlin.

## `net.rubygrapefruit.native-cli-app`

Builds a command-line application implemented in Kotlin/Native

- Adds macOS and Linux targets.
- Adds `nativeMain` and `nativeTest` source sets that are shared by these targets. You can add shared Kotlin/Native code
  to these source sets.
- Adds `appliction { }` block

## `net.rubygrapefruit.jvm-cli-app`

Builds a command-line application implemented in Kotlin/JVM. Expects that the application uses the JVM module system.

- Adds `appliction { }` block
  - `application.module`
  - `application.mainClass`
- Generates a launcher script in the distribution. This script requires that a compatible JVM can be found on the machine's `$PATH`. 

## `net.rubygrapefruit.embedded-jvm`

Can be applied with `net.rubygrapefruit.jvm-cli-app` to include a copy of the JVM in the application distribution,
instead of using a launcher script.

## Common application settings

- Adds `appliction { }` block
  - `application.imageDirectory`
- Adds a `dist` task to create a distribution image ready for installation. By default, this is created in `build/dist-image`

See [`../test-apps`](../test-apps/) for some samples.

### TODO

- Set JDK to 11
- Use modules for all JDK apps
- Add a settings plugin that add the maven repo and plugin dependencies to the root project.
- Add some kind `assembleDebug` task to build a single dev target.
- Add functional tests for plugins.
- Generate windows launcher script for JVM applications.
- Gradle issues:
  - Fix task.flatMap { it.someOutputDir.file("some-file") } -> does not carry task dependencies
  - Some kind of managed type `Property<T>` or `Provider<T>` that allows deferred configuration
