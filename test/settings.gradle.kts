include("native-cli-app")

dependencyResolutionManagement {
    versionCatalogs {
        create("versions") {
            plugin("nativeCliApp", "net.rubygrapefruit.native-cli-app").version("0.1")
        }
    }
}
