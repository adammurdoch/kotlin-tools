import net.rubygrapefruit.app.NativeMachine

plugins {
    id("net.rubygrapefruit.gradle-plugin")
}

group = "net.rubygrapefruit.plugins"

val nativeBinaries = listOf(NativeMachine.MacOSX64, NativeMachine.MacOSArm64).map { machine ->
    val nativeBinaries = configurations.create("nativeBinaries${machine.name}") {
        attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named("native-binary-${machine.kotlinTarget}"))
        isCanBeResolved = true
        isCanBeConsumed = false
    }
    dependencies {
        add(nativeBinaries.name, project(":native-launcher"))
    }
    Pair(machine, nativeBinaries)
}

dependencies {
    implementation("net.rubygrapefruit.libs:download:1.0")
}

tasks.processResources {
    for (entry in nativeBinaries) {
        from(entry.second) {
            into(entry.first.kotlinTarget)
        }
    }
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
