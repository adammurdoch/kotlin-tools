- Required option + unknown parameter -> reports missing option
- Don't run side effects when successful but there are extra inputs
- Split out recovery logic into a parse state implementation
- Distinguish between failure due to missing value vs semantic issue (currently uses `expectedMore`)
- List option:
    - Require 1 or more
- `Action.run()` runs nested actions, if present? Downside: IDEA marks the action field as unused
- Report multiple problems
- Error message shows where on the command-line the problem occurred.
- Use Kotlinx IO types for files
- Improve error reporting when empty argument is found and not expected
- Add some kind of integration with app plugins to supply app name
- Fix error message when multiple actions are provided, either as names or options or combined with default
- Merge `NestedActionHelpAction` into `HelpAction`
- Move calculation of effective usage into `AbstractActionParameter`
- Move handling of option actions to help
- Clean up and test usage types
- `-<value>` option
- Optional argument for option
- Range values
- Enum values
- Map a parameter to a sealed result type that indicates where the value comes from, eg a default, explicit, the literal used, etc
- Help
    - Show `<action>?` in usage when action has a default
    - Allow display name for parameter to be specified, eg `--download <url>` instead of `--download <value>`
    - Don't allow `--help <action>` if there are no actions
    - Fix help when action is not the first positional
    - Use choice for action parameter to `help <action>`
    - Show options for action
    - Show global options for action
    - Show path to action in usage for action, eg `usage: cmd [options] action1 ...`
    - `help` nested action
    - Group long and short choice flags
    - Sort short options first?
    - Help for options, actions, parameters, etc
    - Help should indicate parameter is optional
    - Help for nested actions with nested actions
    - Show required options in synopsis
    - Show examples
    - Show link to documentation
- Completion
    - Fix global flags for nested actions
    - Include help message for each choice completion option
    - Include help message for each action completion option
    - Merge choice options for optional parameter with choice options for next parameter
    - Choice options
    - Boolean positional parameters and options
    - Options with parameter, file and non-file
    - Directory options and parameters
    - Action options
    - Bash completion: https://mill-build.org/blog/14-bash-zsh-completion.html
- Escape names for completion function, actions, help, etc
- Usage message on parse error
    - Suggest `--help`, and special case when `help` is accidentally used instead.
    - Unknown option - show available options
    - Unknown option for sub-command - show available options
    - Unknown action provided to `help`
    - Unknown option - if expecting an action, check if option belongs to action and give hint to use action
    - Argument not provided
    - Too many arguments
    - Option value badly formed
    - Parameter value badly formed
    - Option value missing
    - `-x` used instead of `--x`
- Allow actions to calculate the set of options dynamically
    - For example, action is constructed with a list of enum _types_
- Validate names do not contain spaces
- Validate at least one action or choice is defined, if no default
- Validate choices do not contain spaces
- Check for conflicting options in nested actions
- Disallow conflicting flags, option and action names, choice flags
- Disallow any positional parameters after string list parameter
- File locations: dir, file, must exist
- Color output on terminal
- Declare input environment variables
- Fuzzy matching and/or suggestions
    - Unknown action
    - Unknown option or value
    - Unknown file/directory

Combinators

`option(name): Option<String?>`
`option(name).boolean(): Option<Boolean>`
`parameter(name): Parameter<String>`
`literal(text): Parser<Unit>`
`allOf(producer): Parser<T>`
`allOf(a, map): Parser<T>`
`allOf(a, b, map): Parser<T>`
`allOf(a, b, c, map): Parser<T>`
`oneOf(choices...): Parser<T>`

CLI app

`parser.runApp(args)`

- Color output

- `oneOf()` with overlapping options, some with values and some without
- `oneOf()` reports conflicting options or literals
- `oneOf()` reports problems for option (e.g. repeated option, missing value)
- Show usage on parse error
    - Bold text
    - Include app name
    - Include app summary
    - Include 'run with --help', etc
    - Indent choices
    - Show usage of recognized choice(s) only
    - Use a separate option list when there are many options
    - Use a separate command list when there are many commands
    - Allow value name to be provided for `option()`
    - Highlight problematic part of usage?
- Multiple names for `option()`
- `allOf()` with nested `allOf()`
- Parser that collects unrecognized or remainder
- Merge short options
- `--` as end of options
- `--option=<value>` (allows value that starts with `-`)
- `--no-x` flag for boolean options
- Report `[..]` - optional, `<..>` - required
- `command()`
- Convenience for help command or option
- Convenience for `--version`
- Convenience for `--stack`
- Convenience for completion command or option
- `-Dx=y` style
- Compose value parsers (e.g. value is ip address or host name)
- Deprecated, hidden options + commands
- Terminal integration for JVM
- Terminal integration is thread-safe, e.g. does not interleave partial outputs
