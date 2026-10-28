import net.rubygrapefruit.plugins.app.BuildType.Release
import net.rubygrapefruit.plugins.app.internal.RealizedNativeExecutable

plugins {
    id("net.rubygrapefruit.native.cli-app")
}

group = versions.plugins.group

application {
    macOS {
        dependencies {
            implementation(project(":ui-app-bootstrap"))
        }
    }
}

kotlin {
    macosArm64 {
        compilations.getByName("main") {
            cinterops.create("jni") {
                includeDirs("${System.getProperty("java.home")}/include")
                includeDirs("${System.getProperty("java.home")}/include/darwin")
            }
        }
    }
}

componentRegistry.each<RealizedNativeExecutable> {
    derive { executable ->
        if (executable.canBuildOnHost && executable.buildType == Release) {
            configurations.create("outgoingNativeBinary${executable.machine.alias}") {
                attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named("native-binary-${executable.machine.kotlinTarget}"))
                isCanBeResolved = false
                isCanBeConsumed = true
                outgoing.artifact(executable.binaryFile)
            }
        }
    }
}
