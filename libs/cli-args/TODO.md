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

Combinator API
--------------

- *Command-line arguments* - the values provided on the command-line
- *Parameter* - any value provided to the app from some external source (currently only from command-line arguments)
- *Option* - a parameter that may be provided anywhere in the command-line arguments. 
  - A fixed string that starts with `-`, possibly also followed by zero or more values
- *Positional* - a parameter that may must appear at a certain position in the command-line arguments
- *Parser* - takes command-line arguments and produces some value of type T
- *Literal* - a literal string that must appear at a certain position in the command-line arguments

Combinators
-----------

`option(name): Option<String?>`
`option(name).boolean(): Option<Boolean>`
`positional(name): Positional<String>`
`literal(text): Parser<Unit>`
`params(producer): Parser<T>`
`params(a, map): Parser<T>`
`params(a, b, map): Parser<T>`
`params(a, b, c, map): Parser<T>`
`oneOf(choices...): Parser<T>`

CLI app
-------

`parser.runApp(args)`
- Color output
- Usage summary

- `allOf()` currently skips over a positional that does not match to the next positional
  - See ChoiceOfStringLiteralAndParametersTest, e.g. <literal> <parameter>
- `oneOf()` currently reports each expected literal as a separate problem
  - See ChoiceOfStringLiteralTest
- `oneOf()` with overlapping options, some with values and some without
- `oneOf()` currently does not fail on conflicting options or literals
  - See ChoiceOfStringOptionsTest
- `oneOf()` reports problems for option (e.g. repeated option, missing value)
- Literal currently fails inconsistently when unexpected argument or option is found
  - For unexpected argument, a single "unexpected argument x, expected y" problem is reported
  - For unexpected option, both "expected x" and "unexpected option -x" are reported
- Combine all remaining unexpected arguments into a single failure
  - e.g. instead of "unexpected arg 'a'" + "unexpected arg 'b'" instead produce "unexpected args 'a b'"
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
