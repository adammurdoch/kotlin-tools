plugins {
    id("net.rubygrapefruit.jvm.cli-app")
}

application {
    dependencies {
        implementation(versions.libs.coordinates("cli-app"))
        implementation(versions.libs.coordinates("file-io"))
        implementation(versions.libs.coordinates("parse"))
        implementation(versions.libs.coordinates("file-parse"))
        implementation(project(":kmp-lib-render"))
    }
    test {
        implementation(versions.test.coordinates)
    }
}