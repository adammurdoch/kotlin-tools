package net.rubygrapefruit.machine.cpu;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class Arch {
    /**
     * Returns:
     * 1 - arm64
     * 2 - x64
     */
    private static native int arch();

    public static String getArchitecture() {
        URL url;
        if (System.getProperty("os.arch").equals("aarch64")) {
            url = Arch.class.getClassLoader().getResource("cpu-info-arm64.dylib");
        } else {
            url = Arch.class.getClassLoader().getResource("cpu-info-x64.dylib");
        }
        if (url == null) {
            throw new IllegalStateException("Could not locate native library resource.");
        }
        try {
            Path tmpDir = Files.createTempDirectory("cpu-probe");
            Path library = tmpDir.resolve("cpu-info.dylib");
            try (InputStream inputStream = url.openStream()) {
                Files.copy(inputStream, library);
            }
            System.load(library.toString());
            switch (arch()) {
                case 1:
                    return "arm64";
                case 2:
                    return "x64";
                default:
                    throw new IllegalStateException("Could not determine machine architecture.");
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Could not extract native library", e);
        }
    }
}
