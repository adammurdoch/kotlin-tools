plugins {
    id("net.rubygrapefruit.gradle-plugin")
}

group = "net.rubygrapefruit.plugins"

val nativeBinaries = configurations.create("nativeBinaries") {
    attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named("native-binary"))
    isCanBeResolved = true
    isCanBeConsumed = false
}

dependencies {
    implementation(project(":download"))
    add(nativeBinaries.name, project(":native-launcher"))
}

tasks.processResources {
    from(nativeBinaries)
}

gradlePlugin {
    plugins {
        create("native-binary") {
            id = "net.rubygrapefruit.jvm.native-binary"
            implementationClass = "net.rubygrapefruit.app.plugins.NativeBinaryJvmLauncherPlugin"
        }
        create("jvm-ui-app") {
            id = "net.rubygrapefruit.jvm.ui-app"
            implementationClass = "net.rubygrapefruit.app.plugins.JvmUiApplicationPlugin"
        }
    }
}
