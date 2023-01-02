# Gradle Plugins

A collection of Gradle plugins for building applications and libraries using Kotlin.

These plugins all target Kotlin 1.8.0 and a minimum of Java 11 (for Kotlin/JVM apps). The plugins require
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

## `net.rubygrapefruit.native.ui-app`

Builds a UI application implemented in Kotlin/Native that targets native desktop platforms.

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
- Provides an application icon, default location is `src/main/Icon1024.png`
- Use `dist` task to build the debug application bundle
- Use `releaseDist` to build the release application bundle
- Adds `application { }` block, see below for the available settings.

## `net.rubygrapefruit.gradle-plugin`

A convention plugin for implementing Gradle plugins in Kotlin/JVM.

- Adds dependencies and repository definitions so that a fixed version of Kotlin available (currently 1.8.0).

## Common application settings for all targets

- Adds `application { }` block
  - `application.appName` - the base name for the application, used for various default file names.
  - `application.distribution.imageDirectory` - the directory to create the distribution image in.
- Adds a `dist` task to create a distribution image containing the app ready for installation.
  By default, this is created in `build/dist-image`

## Common application settings for JVM targets

- Adds `application { }` block
  - `application.mainClass` - must be specified.
  - `application.module.name` - defaults to the application name

## Common JVM library settings

- Adds `library { }` block
  - `library.module.name` - defaults to the project name 
  - `library.module.exports` 
  - `library.module.requires` - calculated from compile (implementation) dependencies 
  - `library.module.requiresTransitive` - calculated from the API dependencies

See [`../test-apps`](../test-apps/) for some samples.
