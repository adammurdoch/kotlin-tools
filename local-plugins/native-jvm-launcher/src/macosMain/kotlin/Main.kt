import jni.*
import kotlinx.cinterop.*
import net.rubygrapefruit.app.ui.launcher.failed
import net.rubygrapefruit.app.ui.launcher.runMain
import platform.CoreFoundation.CFRunLoopRunInMode
import platform.CoreFoundation.kCFRunLoopDefaultMode
import platform.Foundation.NSBundle
import platform.Foundation.NSThread
import platform.Foundation.NSTimer
import platform.posix.*

@OptIn(ExperimentalForeignApi::class)
private typealias CreateJavaVM = CFunction<(CPointer<CPointerVar<JavaVMVar>>?, CPointer<COpaquePointerVar>?, COpaquePointer?) -> jint>

fun main(args: Array<String>) {
    runMain {
        println("Current dir: ${getCurrentDir()}")

        val bundlePath = NSBundle.mainBundle.bundlePath
        println("Bundle path: $bundlePath")

        val lines = readConfigFile("$bundlePath/Contents/Resources/launcher.conf")
        val appDisplayName = lines[0]
        val jvmDirPath = lines[1]
        val mainClass = lines[2]
        val startOnMainThread = lines[3].toBooleanStrict()

        println("App display name: $appDisplayName")
        println("Main class: $mainClass")

        if (startOnMainThread) {
            runJvm(bundlePath, jvmDirPath, mainClass, args)
        } else {
            runJvmOnOtherThread(bundlePath, jvmDirPath, mainClass, args)
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun runJvmOnOtherThread(bundlePath: String, jvmDirPath: String, mainClass: String, args: Array<String>) {
    val jvmThread = NSThread {
        runJvm(bundlePath, jvmDirPath, mainClass, args)
    }
    jvmThread.start()

    // Timer prevents run loop from finishing immediately, while waiting for the JVM to create the UI
    NSTimer.scheduledTimerWithTimeInterval(1.0e10, repeats = true) { }
    while (true) {
        CFRunLoopRunInMode(kCFRunLoopDefaultMode, 1.0e20, false)
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun readConfigFile(configFilePath: String): List<String> {
    return memScoped {
        val configFile = fopen(configFilePath, "r") ?: failed("Could not read launcher config file at '$configFilePath'")
        try {
            val size = 1024 * 4
            val buffer = allocArray<ByteVar>(size)
            val nread = fread(buffer, 1.convert(), size.convert(), configFile)
            buffer[nread.convert()] = 0
            buffer.toKString().lines().map { it.trim() }
        } finally {
            fclose(configFile)
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun runJvm(bundlePath: String, jvmDirPath: String, mainClass: String, args: Array<String>) {
    memScoped {
        val jvmLibraryPath = "$bundlePath/Contents/$jvmDirPath/lib/server/libjvm.dylib"
        val libHandle = dlopen(jvmLibraryPath, RTLD_LAZY) ?: failed("Could not open JVM library")
        val address = dlsym(libHandle, "JNI_CreateJavaVM") ?: failed("Could not locate JVM create function")
        val createJavaVM = address.reinterpret<CreateJavaVM>()

        val jvm = alloc<CPointerVar<JavaVMVar>>()
        val jniEnv = alloc<CPointerVar<JNIEnvVar>>()
        val jvmArgs = alloc<JavaVMInitArgs>()

        jvmArgs.version = JNI_VERSION_21
        jvmArgs.nOptions = 0
        jvmArgs.options = null
        jvmArgs.ignoreUnrecognized = 0.convert()

        val result = createJavaVM(jvm.ptr, jniEnv.ptr.reinterpret(), jvmArgs.ptr)
        if (result != JNI_OK) {
            throw RuntimeException("Could not create JVM: $result")
        }

        val env = jniEnv.value
        val functions = jniEnv.pointed!!.pointed!!
        val mainClassHandle = functions.FindClass!!(env, mainClass.replace('.', '/').cstr.ptr)
        if (mainClassHandle == null) {
            throw RuntimeException("Could not load main class $mainClass")
        }
        val mainMethodHandle = functions.GetStaticMethodID!!(env, mainClassHandle, "main".cstr.ptr, "([Ljava/lang/String;)V".cstr.ptr)
        if (mainMethodHandle == null) {
            throw RuntimeException("Could not locate ${mainClass}.main() method")
        }
        val stringHandle = functions.FindClass!!(env, "java/lang/String".cstr.ptr)
        if (stringHandle == null) {
            throw RuntimeException("Could not locate Java String class")
        }
        val mainMethodArgs = functions.NewObjectArray!!(env, args.size, stringHandle, null)
        args.forEachIndexed { index, arg ->
            functions.SetObjectArrayElement!!(env, mainMethodArgs, index, functions.NewStringUTF!!(env, arg.cstr.ptr))
        }
        val callArgs = allocArray<jvalue>(1)
        callArgs[0].l = mainMethodArgs
        functions.CallStaticVoidMethodA!!(env, mainClassHandle, mainMethodHandle, callArgs)

        val hasException = functions.ExceptionCheck!!(env) != 0.toUByte()
        if (hasException) {
            functions.ExceptionDescribe!!(env)
        }

        val vm = jvm.value!!
        vm.pointed.pointed!!.DestroyJavaVM!!(vm)

        if (hasException) {
            exit(1)
        } else {
            exit(1)
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun getCurrentDir(): String {
    return memScoped {
        val length = MAXPATHLEN
        val buffer = allocArray<ByteVar>(length)
        val path = getcwd(buffer, length.convert()) ?: failed("Could not get current directory.")
        path.toKString()
    }
}
