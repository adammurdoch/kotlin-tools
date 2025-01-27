# Kotlin tools CLI application library

A small framework to help implement CLI applications using Kotlin multiplatform.

Targets:

- JVM 11+
- Browser
- MacOS x64 and arm64
- Windows x64
- Linux x64

Extends the cli-args library and adds support for:

- Help messages and help actions
- ZSH command-line completion
- Process exit code on success or failure
- Option to show or hide stack traces on failure
- File and directory parameters and options

## Concepts

An application is similar to a function: it takes some parameters as input and does something useful based on those parameters.
The cli-app library supports two types of parameters:

- *Positional parameters*, which are those identified by their position on the command-line. For example, `cp src dest` takes two positional parameters with values `src` and `dest` respectively.
- *Named parameters*, which are those identified by a name with some special prefix. For example, `java -classpath some.jar my.Class` takes a named parameter called `classpath` with value `some.jar`. It also takes a positional parameter with value `my.Class`.

Each parameter takes a value. The cli-app library supports parameter values of various types, such as `String`, `Boolean` or `Int`. It also supports actions, or "sub-commands" as a value. For example, `git commit -m "bug fixes"` takes a `commit` action with a named parameter. An action can have its own parameters, including actions.

## Usage

To use, declare a dependency on cli-app:

```
dependencies {
    implementation("{{project.coordinates}}")
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

A positional parameter is identified by its position on the command-line.

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

You pass the parameter value to the application by adding the value on the command-line:

```
> myapp world
Hello world
```

By default, a positional parameter is required and the app will fail when the parameter is not provided (and provide a nice error message to help the user figure out what to do next).
You can make a parameter optional using `parameter().optional()`, in which case it will have a `null` value when the parameter is not provided, or `parameter().whenAbsent()`, in which case it will have a default value when the parameter is not provided.

### Options

An option is a named parameter that is, as the name suggests, optional. To add an option, use the `option()` function:

```kotlin
class MyApp: CliApp("myapp") {
    // Defines an option
    private val name by option("name")
    
    override fun run() {
        println("Hello $name")
    }
}
```

You pass the parameter value to the application using the parameter name prefixed by `--`, followed by the parameter value:

```
> myapp --name world
Hello world
```

An option will have a `null` value when the option is not provided:

```
> myapp
Hello null
```

You can use `option().whenAbsent()` to use a default value when the option is not provided.
You can make the option required using `option().required()`, in which case the app will fail when the option is not provided.

### Flags

A flag is an optional named parameter with boolean type.

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

You pass the parameter value to the application using the parameter name prefixed by `--`:

```
> myapp --verbose
Hello world
> myapp
> myapp --no-verbose
```

A flag will have value `false` when the flag is not provided:

```
> myapp
```

You can also use the parameter name prefixed by `--no-` to explicitly disable the flag:

```
> myapp --no-verbose
```

### Actions

An action is an operation or sub-command that your application can run. Actions can have their own parameters, options, nested actions, etc.

```kotlin
class MyApp: CliApp("myapp") {
    // Define a set of actions to choose from 
    private val action by action {
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

An action is a special kind of positional parameter. As such, you can mix actions with other positional and named parameters.
Like other positional parameters, by default an action is required, but you can make it optional using `optional()` or `whenAbsent()`.

### Multiple values

Use the `parameter().repeated()` function to add a positional parameter that can be repeated to produce a list.
Use the `option().repeated()` function to add a named parameter that can be repeated to produce a list.

Use the `remainder()` function to add a positional parameter that consumes the remainder of the command-line, including all options and flags.

### Value types

Use the `int()` and `boolean()` functions to add parameters with integer and boolean types.
Use the `oneOf()` function to add parameters that select a value from a set of values.
Use the `type { }` function to add parameters with a custom type.
