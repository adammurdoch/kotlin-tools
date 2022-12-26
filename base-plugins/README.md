# Gradle Plugins

A collection of Gradle plugins for building applications and libraries using Kotlin.

These plugins all target Kotlin 1.7.20 and a minimum of Java 11 (for Kotlin/JVM apps). The plugins require
Gradle 7.5.1 or later.

## `net.rubygrapefruit.kotlin-apps`

A settings plugin that must be applied to the settings of any build that uses these plugins.

## `net.rubygrapefruit.native.lib`

Builds a library implemented in Kotlin/Native that targets native desktop platforms.

- Adds Linux and Windows x64 targets and macOS x64 and arm64 targets.
- Adds `unixMain` source sets for Kotlin code that is shared by macOS and Linux targets.
- Adds `macosMain` source set for Kotlin code that is shared by macOS targets.

## `net.rubygrapefruit.native.cli-app`

Builds a command-line application implemented in Kotlin/Native that targets native desktop platforms.

- Adds Linux and Windows x64 targets and macOS x64 and arm64 targets.
- Adds `unixMain` and `unixTest` source sets for Kotlin code that is shared by macOS and Linux targets.
- Adds `macosMain` and `macosTest` source set for Kotlin code that is shared by macOS targets.
- Adds `application { }` block, see below for the available settings.

## `net.rubygrapefruit.mpp.lib`

Builds a library implemented in Kotlin multiplatform that targets the JVM and native desktop platforms.

- Add JVM target.
- Adds Linux and Windows x64 targets and macOS x64 and arm64 targets.
- Adds `nativeMain` source set for Kotlin code that is shared by native targets.
- Adds `library { }` block, see below for the available settings.
- Generates a `module-info` for the JVM target.

## `net.rubygrapefruit.jvm.lib`

Builds a library implemented in Kotlin/JVM.

- Adds `library { }` block, see below for the available settings.
- Generates a `module-info`.

## `net.rubygrapefruit.jvm.cli-app`

Builds a command-line application implemented in Kotlin/JVM.
Expects that the application uses the JVM module system.

- The distribution contains the jars for the application and a launcher script. 
  This script requires that a compatible JVM can be found on the machine's `$PATH`. 
- Adds `application { }` block, see below for the available settings.
- Generates a `module-info`.

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
- Use `dist` task to build the debug application bundle
- Use `releaseDist` to build the release application bundle
- Adds `application { }` block, see below for the available settings.

## `net.rubygrapefruit.plugin`

A convention plugin for implementing Gradle plugins in Kotlin/JVM.

- Adds dependencies and repository definitions so that a fixed version of Kotlin available (currently 1.7.20).

## Common application settings for all targets

- Adds `application { }` block
  - `application.appName` - the base name for the application, used for various default file names.
  - `application.imageDirectory` - the directory to create the distribution image in.
- Adds a `dist` task to create a distribution image containing the app ready for installation.
  By default, this is created in `build/dist-image`

## Common application settings for JVM targets

- Adds `application { }` block
  - `application.module.name` - defaults to the application name
  - `application.mainClass` - must be specified.

## Common JVM library settings

- Adds `library { }` block
  - `library.module.name` - defaults to the project name 
  - `library.module.exports` 
  - `library.module.requires` - calculated from the compile (implementation) dependencies 
  - `library.module.requiresTransitive` - calculated from the API dependencies

See [`../test-apps`](../test-apps/) for some samples.

### TODO

- Native-launcher: build both macOS binaries, do not build on non-macOS machine
- Don't include windows launcher script for embedded JVM and native exe JVM apps
- Fix native image for windows
- Don't add a dist task when there is no source for the current target (eg native-launcher on linux)
- Download service: don't keep the install dir when install action fails
- Set JDK to correct architecture somehow?
- Use `jlink` from arm64 toolchain on m1 macOS
- Native app plugin creates distributions for all targets
- Native macOS bundle plugin
- Configurable bundle id, app display name, app version
- Infer the main class for a JVM app
- Module-info
  - Default module name to top-most package
  - Only export packages that contain public non-internal types
  - Figure out how to enforce for Kotlin JVM compilation
  - Use an artifact transform to extract the module info for dependencies
- Use correct architecture for macOS native binary, embedded JVM and GraalVM native binary output.
  - Bundle native launcher for each platform in the plugins.
- Add some kind `assembleDebug` task to build a single dev target.
- Add functional tests for plugins.
  - Customisation
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
