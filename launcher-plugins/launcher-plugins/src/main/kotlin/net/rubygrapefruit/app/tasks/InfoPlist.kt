package net.rubygrapefruit.app.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import kotlin.io.path.writeText

abstract class InfoPlist : DefaultTask() {
    @get:OutputFile
    abstract val plistFile: RegularFileProperty

    @get:Input
    abstract val bundleIdentifier: Property<String>

    @get:Input
    abstract val executableName: Property<String>

    @get:Input
    abstract val bundleName: Property<String>

    @TaskAction
    fun generate() {
        val infoPlist = plistFile.get().asFile.toPath()
        infoPlist.writeText("""
            <?xml version="1.0" encoding="UTF-8"?>
            <!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
            <plist version="1.0">
                <dict>
                    <key>CFBundleIdentifier</key>
                    <string>${bundleIdentifier.get()}</string>
                    <key>CFBundleName</key>
                    <string>${bundleName.get()}</string>
                    <key>CFBundlePackageType</key>
                    <string>APPL</string>
                    <key>CFBundleExecutable</key>
                    <string>${executableName.get()}</string>
                </dict>
            </plist>            
        """.trimIndent())
    }
}