# Gradle Plugins

A collection of Gradle plugins for building applications using Kotlin.

These plugins all target Kotlin 1.7.10 and a minimum of Java 11 (for Kotlin/JVM apps). The plugins require
Gradle 7.5.1 or later.

## `net.rubygrapefruit.kotlin-apps`

A settings plugin that must be applied to the settings of any build that uses these plugins.

## `net.rubygrapefruit.native.lib`

Builds a library implemented in Kotlin/Native.

- Adds macOS and Linux targets.

## `net.rubygrapefruit.native.cli-app`

Builds a command-line application implemented in Kotlin/Native.

- Adds macOS and Linux targets.
- Adds `nativeMain` and `nativeTest` source sets that are shared by these targets. You can add shared Kotlin/Native code
  to these source sets.
- Adds `application { }` block, see below for the available settings.

## `net.rubygrapefruit.jvm.lib`

Builds a library implemented in Kotlin/JVM.

## `net.rubygrapefruit.jvm.cli-app`

Builds a command-line application implemented in Kotlin/JVM.
Expects that the application uses the JVM module system.

- The distribution contains the jars for the application and a launcher script. 
  This script requires that a compatible JVM can be found on the machine's `$PATH`. 
- Adds `application { }` block, see below for the available settings.

## `net.rubygrapefruit.jvm.embedded-jvm`

Can be applied with `net.rubygrapefruit.jvm.cli-app` to include a copy of the JVM in the application distribution,
instead of using a launcher script. Uses `jlink` to create the JVM image.

## `net.rubygrapefruit.jvm.native-binary`

Can be applied with `net.rubygrapefruit.jvm.cli-app` to create a native binary to use in the application distribution,
instead of using a launcher script. Uses `graalvm` to create the native binary.

## `net.rubygrapefruit.jvm.ui-app`

Builds a desktop UI application implemented in Kotlin/JVM and that presents a Swing or JavaFX UI.
Expects that the application uses the JVM module system.

- Creates a debug (unsigned) macOS application bundle and a release (signed and notarized) application bundle
- Provides an application icon
- Use `dist` task to build the debug application bundle and `releaseDist` to build the release application bundle
- Adds `application { }` block, see below for the available settings.

## `net.rubygrapefruit.plugin`

A convention plugin for implementing Gradle plugins using Kotlin.

- Adds dependencies and repository definitions so that a fixed version of Kotlin available (currently 1.7.10).

## Common application settings

- Adds `application { }` block
  - `application.appName` - the base name for the application, used for various default file names.
  - `application.imageDirectory` - the directory to create the distribution image in.
- Adds a `dist` task to create a distribution image containing the app ready for installation.
  By default, this is created in `build/dist-image`

## Common JVM application settings

- Adds `application { }` block
  - `application.module` - defaults to the application name
  - `application.mainClass` - must be specified.

See [`../test-apps`](../test-apps/) for some samples.

### TODO

- Split base plugin out of jvm cli plugin to share with jvm ui plugin
- Configurable bundle id, app display name
- Fix native image for linux and windows
- Infer the main class for a JVM app.
- Set JDK to 11.
- Use toolchain's `jlink` command.
- Generate module-info with requires.
- Use correct architecture for macOS native binary, embedded JVM and GraalVM native binary output.
  - Bundle native launcher for each platform in the plugins.
- Add a settings plugin that add the maven repo and plugin dependencies to the root project.
- Add some kind `assembleDebug` task to build a single dev target.
- Add functional tests for plugins.
  - Customisation
- Generate windows launcher script for JVM applications.
- Refactor `DownloadRepository` to use Gradle exec service when used in a plugin.
- Gradle issues:
  - Fix import of `test-apps` - does not seem to be included
  - Use version catalog
  - "Included build 'x' does not exist" does not give any content, e.g. where is it defined?
  - Get the jlink tool for a toolchain
  - Add some way to get a dependency on all tasks with a given name in a build
  - Add some way to get a dependency on task with given path in a build, where the task may not exist
  - Fix running `gradlew test-apps:x` (needs leading `:` and error message does not mention this) 
  - Fix `task.flatMap { it.someOutputDir.file("some-file") }` -> does not carry task dependencies
  - Awkward to discard property value from Kotlin (set(null) is ambiguous)
  - Set property using `Path`.
  - Some kind of managed type `Property<T>` or `Provider<T>` that allows deferred configuration (eg `Property<Distribution>`)
  - Create a property of managed type (eg `val dist: Distribution`)
  - Some way to get `FileCollection` elements as locations or file names (ie without the build dependencies, to generate a manifest, etc.)
  - No way to have a generated output directory (eg a task that uses `DownloadRepository` to locate the output)
  - Improve error message when settings plugin is applied to project, and vice versa
  - Some way to apply concurrent limits across all builds (eg to work around `commonizeNativeDistribution` concurrency issue)
