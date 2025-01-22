import net.rubygrapefruit.plugins.app.Versions

plugins {
    id("net.rubygrapefruit.bootstrap.gradle-plugin")
}

group = Versions.plugins.group

dependencies {
    implementation("org.commonmark:commonmark:0.22.0")
}

gradlePlugin {
    plugins {
        create("docs") {
            id = "net.rubygrapefruit.bootstrap.docs"
            implementationClass = "net.rubygrapefruit.plugins.docs.internal.DocsPlugin"
        }
    }
}
