# Bootstrap plugins

Gradle plugins that can build the Kotlin tools plugins and the JVM libraries that they use as dependencies.

These libraries are pre-alpha.

- [`bytecode`](bytecode/): A library to read and write JVM bytecode
- [`machine-info`](machine-info/): A library to inspect the host machine.

## Releasing

- Create and upload GPG signing key, see https://central.sonatype.org/publish/requirements/gpg/
- Create Maven central token via portal at https://central.sonatype.com/account (see https://central.sonatype.org/publish/generate-portal-token/)
- Set `$MAVEN_CENTRAL_USERNAME` and `$MAVEN_CENTRAL_TOKEN` environment variables using values from the previous step
- Create Github personal access token via "Settings > Developer settings" (see https://docs.github.com/en/rest/releases/releases?apiVersion=2022-11-28#create-a-release)
- Set `$GITHUB_TOKEN` environment variable using value from the previous step
- Run `./gradlew release -Drelease.type=final` for a final release
  - Don't include `-Drelease.type` for a milestone release
- Publish release on Maven Central via portal
- Update and run samples: `./gradlew samples verifySamples`
- Update and generate docs: `./gradlew docs`
- Commit updates and push
- Update Github release
