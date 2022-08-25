include("conventions")

dependencyResolutionManagement {
    versionCatalogs {
        create("versions") {
            plugin("kotlinJvmPlugin", "org.jetbrains.kotlin.jvm").version("1.7.10")
            library("kotlinJvmPlugin", "org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")
        }
    }
}
