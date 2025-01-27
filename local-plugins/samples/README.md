
- Looks for samples in `src/samples`
- Run `samples` to generate samples for most recently released version.
- Run `verifySamples` to verify them.
- Run `localSamples` to generate samples for the version being built.
- Can use macros in build script:
  - `samples.multiplatform()` - adds boilerplate to build KMP app.
  - `samples.coordinates()` - the coordinates of the target version.
