- Move usage generation to cli-app
- Clean up API for `simpleFlag()`
- Clean up API for `parseAll()` and hide `ParseResult`
- Clean up usage types.
- `--help` should group long and short choice flags
- Help for options, actions, parameters, etc
- Completion
    - Choice positional parameters and options
    - Boolean positional parameters and options
    - Multi-value positional parameters
    - Options with parameter, file and non-file
    - Action options
    - Global options inside action
- Optional parameter, eg <choice>? <choice>?
- Escape names for completion function, actions, help, etc
- `--help` shows usage of actions
- Usage sorts options using name, rather than `--name` or `-n`
- Usage message on parse error
    - Suggest `--help`
    - Unknown option - show available options
    - Unknown option for sub-command - show available options
    - Argument not provided
    - Too many arguments
    - Option value badly formed
    - Parameter value badly formed
    - Option value missing
- Handle required list parameter with default value - disallow? succeed if absent and default provided?
- parameter().whenAbsent().int() -> what should happen to the default? currently is discarded
- Validate names do not contain spaces
- Validate at least one action or choice is defined
- Validate choices do not contain spaces
- Validate choices are not options
- Disallow conflicting flags, option and action names, choice flags
- Disallow any positional parameters after `arguments()`
- File locations: dir, file, must exist
- Color output on terminal
- Fuzzy matching and/or suggestions

```
string().option("f") -> (-f <value>)? nullable
int().option("f") -> (-f <value>)? nullable
path().option("f") -> (-f <value>)? nullable
boolean().option("f") -> (-f)? nullable
oneOf { }.option("f) -> (-f <value>)? nullable
custom().option("f") -> (-f <value>)? nullable

string().option("f").whenAbsent("v") -> (-f <value>)? not-null

convenience:
option("f") -> string().option("f")
option("f).whenAbsent("v")

oneOf { }.flags() -> flag, no flag
boolean().flag("flag") -> flag, no flag

convenience:
flag("flag") -> boolean.flag("flag")

string().parameter() -> <value> not-null
int().parameter() -> <value> not-null
path().parameter() -> <value> not-null
boolean().parameter() -> <value> not-null
oneOf { }.parameter() -> <value> not-null
custom().parameter() -> <value> not-null

string().parameter().whenAbsent("v") -> (<value>) not-null

string().list().parameter() -> <value>*, not-null
string().list().parameter().required() -> <value>+, not-null
```

`compinit` - initialise completion
`compdef <fun> <name>` - use function to complete command or context (e.g. for shell syntax completion) with given name

- completion context, contains
    - first field is `completion`
    - completer function that controls completion
    - command or context
    - argument number being completed
    - tag (type of value being completed)
- pressing ^x h shows context and tags at current position
- completion process
    - creates blank context, progressively fills it in
    - for sub-commands, can apparently change the "command" field in the context to a new name to do completion for the sub-command
    - use styles to match against the context
- standard tags
    - `arguments`, `options`
    - `commands` - can be used for subcommands?
    - `files`, `directories`
- utility functions
    - wrappers around `compadd` built-in (https://zsh.sourceforge.io/Doc/Release/Completion-Widgets.html#Completion-Widgets)
    - `_arguments`
