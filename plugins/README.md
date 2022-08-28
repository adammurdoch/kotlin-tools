# Gradle Plugins

A collection of Gradle plugins for building applications using Kotlin.

## `net.rubygrapefruit.kotlin-apps`

A settings plugin that must be applied to the settings of any build that uses these plugins.

## `net.rubygrapefruit.native-cli-app`

Builds a command-line application implemented in Kotlin/Native

- Adds macOS and Linux targets.
- Adds `nativeMain` and `nativeTest` source sets that are shared by these targets. You can add shared Kotlin/Native code
  to these source sets.
- Adds `appliction { }` block

## `net.rubygrapefruit.jvm-cli-app`

Builds a command-line application implemented in Kotlin/JVM. Expects that the application uses the JVM module system.

- Adds `application { }` block
  - `application.module`
  - `application.mainClass`
- Generates a launcher script in the distribution. This script requires that a compatible JVM can be found on the machine's `$PATH`. 

## `net.rubygrapefruit.embedded-jvm`

Can be applied with `net.rubygrapefruit.jvm-cli-app` to include a copy of the JVM in the application distribution,
instead of using a launcher script. Uses `jlink` to create the JVM image.

## `net.rubygrapefruit.native-binary`

Can be applied with `net.rubygrapefruit.jvm-cli-app` to create a native binary to use in the application distribution,
instead of using a launcher script. Uses `graalvm` to create the native binary.

## Common application settings

- Adds `application { }` block
  - `application.appName`
  - `application.imageDirectory`
- Adds a `dist` task to create a distribution image ready for installation. By default, this is created in `build/dist-image`

See [`../test-apps`](../test-apps/) for some samples.

### TODO

- Set JDK to 11.
- Use toolchain's `jlink` command.
- Generate module file.
- Use correct architecture for macOS native binary and embedded JVM
- Add a settings plugin that add the maven repo and plugin dependencies to the root project.
- Add some kind `assembleDebug` task to build a single dev target.
- Add jvm library convention plugin and use for `downloads` project.
- Add plugin convention plugin and use for plugins projects.
- Add functional tests for plugins.
  - Customisation
- Generate windows launcher script for JVM applications.
- Gradle issues:
  - Fix import of `test-apps` 
  - Use version catalog
  - Fix running `gradlew test-apps:x` (needs leading `:` and error message does not mention this) 
  - Fix `task.flatMap { it.someOutputDir.file("some-file") }` -> does not carry task dependencies
  - Awkward to discard property value from Kotlin (set(null) is ambiguous)
  - Set property using `Path`.
  - Some kind of managed type `Property<T>` or `Provider<T>` that allows deferred configuration (eg `Property<Distribution>`)
  - Create a property of managed type (eg `val dist: Distribution`)
  - Some way to get `FileCollection` elements as locations or file names (ie without the build dependencies, to generate a manifest, etc.)
  - No way to have a generated output directory (eg a task that uses `DownloadRepository` to locate the output)
