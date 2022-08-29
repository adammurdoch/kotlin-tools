package net.rubygrapefruit.app.plugins

import net.rubygrapefruit.app.internal.settingsPluginApplied
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import java.util.concurrent.locks.ReentrantLock

class KotlinBasePlugin: Plugin<Settings> {
    override fun apply(target: Settings) {
        with(target) {
            target.gradle.rootProject { project ->
                with(project) {
                    buildscript.repositories.mavenCentral()
                    buildscript.dependencies.add("classpath", "org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")

                    // Need this to resolve native tooling
                    repositories.mavenCentral()

                    afterEvaluate {
                        val task = tasks.findByName("commonizeNativeDistribution")
                        if (task != null) {
                            val lock = globalLock
                            task.doFirst {
                                println("-> ###### LOCKING FOR ${task.path}")
                                lock.lock()
                            }
                            task.doLast {
                                println("-> ###### UNLOCKING FOR ${task.path}")
                                lock.unlock()
                            }
                        }
                    }

                    settingsPluginApplied()
                }
            }
        }
    }

    private val globalLock: ReentrantLock
        get() {
            return System.getProperties().getOrPut("__LOCK__") { ReentrantLock() } as ReentrantLock
        }
}