plugins {
    id("org.jetbrains.kotlin.jvm").version("1.7.10").apply(false)
}

for (task in listOf("clean", "assemble", "check", "build")) {
    project.tasks.register(task) {
        dependsOn(subprojects.map { it.tasks.findByName(task) }.filterNotNull())
    }
}

project.tasks.register("dist") {
}