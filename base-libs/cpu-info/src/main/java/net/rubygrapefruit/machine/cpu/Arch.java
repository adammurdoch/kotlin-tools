package net.rubygrapefruit.machine.cpu;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class Arch {
    private static String arch;

    private static native String arch();

    public static synchronized String getMacOsArchitecture() {
        if (arch == null) {
            URL url = locateDynamicLibrary();
            Path library = extractLibrary(url);

            System.load(library.toString());
            arch = arch();
        }
        return arch;
    }

    private static Path extractLibrary(URL url) {
        try {
            Path tmpDir = Files.createTempDirectory("cpu-probe");
            Path library = tmpDir.resolve("cpu-info.dylib");
            try (InputStream inputStream = url.openStream()) {
                Files.copy(inputStream, library);
            }
            library.toFile().deleteOnExit();
            return library;
        } catch (IOException e) {
            throw new UncheckedIOException("Could not extract native library.", e);
        }
    }

    private static URL locateDynamicLibrary() {
        URL url;
        if (System.getProperty("os.arch").equals("aarch64")) {
            url = Arch.class.getClassLoader().getResource("cpu-info-arm64.dylib");
        } else {
            url = Arch.class.getClassLoader().getResource("cpu-info-x64.dylib");
        }
        if (url == null) {
            throw new IllegalStateException("Could not locate dynamic library resource.");
        }
        return url;
    }
}
