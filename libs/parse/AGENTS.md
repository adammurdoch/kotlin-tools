# AGENTS.md

See `README.md` for the public API and `DEVELOPMENT.md` for the checklist to follow when adding new parser functions.

## Verifying changes

From the repo root:

- `./gradlew :libs:parse:jvmTest` — fastest feedback loop, use this while iterating.
- `./gradlew :libs:parse:allTest` — test all targets.

Use `--tests "fully.qualified.ClassName"` to scope a run to one test class.

Test failure details aren't in the console summary — read the XML report at
`build/test-results/jvmTest/TEST-<ClassName>.xml` for the actual assertion diff/message.
