Stage 2
-------

Plugins implemented in Kotlin that allow libraries and Gradle plugins implemented in Kotlin to be both used at build time and published. 

Gradle plugin plugin
    - Produces *production* plugins
    - Extends stage 1 plugin to allow source from another project
Jvm library plugin
    - Produces *production* JVM libraries
Serialization plugin
    - Also adds JSON as an implementation dependency
Settings plugin
    - Extends stage 1 plugin
Included build plugin
    - Adds lifecycle tasks
