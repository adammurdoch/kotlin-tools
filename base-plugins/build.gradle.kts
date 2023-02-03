plugins {
    id("org.jetbrains.kotlin.jvm").version("1.8.10").apply(false)
}

for (task in listOf("clean", "assemble", "check", "build")) {
    project.tasks.register(task) {
        dependsOn(subprojects.mapNotNull { it.tasks.findByName(task) })
    }
}

project.tasks.register("dist") {
}