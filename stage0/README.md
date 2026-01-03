Stage 0
-------

Plugins implemented using Java that provide basic support for writing non-released Gradle plugins using Java:

- Build constants plugin
  - Provides access to build constants via an extension and API
  - These constants are derived from `versions.toml`
- Java Gradle plugin plugin
  - Support for implementing Gradle plugins that are:
    - Implemented using Java
    - Not released
  - Does not provide a DSL

These plugins are not released and are intended to be used only by this build.