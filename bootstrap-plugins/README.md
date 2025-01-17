# Bootstrap plugins

Gradle plugins that can build the Kotlin tools plugins and the JVM libraries that they use as dependencies.

These plugins are not intended to be used outside of this repository.

## Releasing

- Create and upload GPG signing key, see https://central.sonatype.org/publish/requirements/gpg/
- Create Maven central token, see https://central.sonatype.org/publish/generate-portal-token/
- Set `$MAVEN_CENTRAL_USERNAME` and `$MAVEN_CENTRAL_TOKEN` environment variables using values from the previous step
