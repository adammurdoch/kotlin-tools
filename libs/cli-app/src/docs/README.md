# Kotlin tools CLI application library

A small framework to help implement CLI applications using Kotlin multiplatform.

Targets:

- JVM 11+
- Browser
- MacOS x64 and arm64
- Windows x64
- Linux x64

Extends the cli-args library and adds support for:

- Help messages
- ZSH command-line completion
- Process exit code on success or failure
- Show or hide stack traces on failure
- File and directory parameters and options

## Usage

To use, declare a dependency on cli-app:

```
dependencies {
    implementation("net.rubygrapefruit:cli-app:0.0.1")
}
```

Create a class that extends `CliApp` and overrides the `run()` function to provide the application's implementation:

```kotlin
class MyApp: CliApp("myapp") {
    override fun run() {
        println("Hello world")
    } 
}
```

You should also implement a `main()` method that delegates to the application class:

```kotlin
fun main(args: Array<String>) = MyApp().run(args)
```

### Positional parameters

A positional parameter is a parameter that your application takes as input and that is identified by its position on the command-line.

To add a positional parameter, use the `parameter()` function:

```kotlin
class MyApp: CliApp("myapp") {
    // Defines a parameter
    private val name by parameter("name")
    
    override fun run() {
        println("Hello $name")
    }
}
```

```
> myapp world
Hello world
```

By default, a positional parameter is required and the app will fail when the parameter is not provided.
You can make a parameter optional using `optional()`, which returns `null` when the parameter is not provided,
or `whenAbsent()`, which returns a default value when the parameter is not provided.

### Options

To add an option that takes a parameter, use the `option()` function:

```kotlin
class MyApp: CliApp("myapp") {
    // Defines an option
    private val name by option("name")
    
    override fun run() {
        println("Hello $name")
    }
}
```

```
> myapp --name world
Hello world
```

An option is optional, and its value is `null` when the option is not provided.
You can use `whenAbsent()` to return a default value when the option is not provided.

### Flags

A flag is a boolean option.

```kotlin
class MyApp: CliApp("myapp") {
    // Defines a flag
    private val verbose by flag("verbose")
    
    override fun run() {
        if (verbose) {
            println("Hello world")
        }
    }
}
```

```
> myapp --verbose
Hello world
> myapp
> myapp --no-verbose
```

### Actions

An action is a nested operation or subcommand that your application can run. Actions can have their own parameters, options, nested actions, etc.

```kotlin
class MyApp: CliApp("myapp") {
    // Define a set of actions to choose from 
    private val action by actions {
        action(AddItem(), "add")
        action(ListItems(), "list")
    }
    
    override fun run() {
        // Run the selected action
        action.run()
    }
}

class AddItem: Action() {
    private val item by parameter("name")
    
    override fun run() {
        // ...
    }
}

class ListItems: Action() {
    override fun run() {
        // ...
    } 
}
```

```
> myapp add item
> myapp list
```

This acts as a special kind of positional parameter.
