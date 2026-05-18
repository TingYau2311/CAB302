package au.edu.qut.cogniti;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public final class Secrets {

    private static final Properties properties = new Properties();

    static {
        try (FileInputStream fis = new FileInputStream("secrets.properties")) {
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to load secrets.properties. " +
                            "Make sure the file exists and is readable.", e
            );
        }
    }

    private Secrets() {
        // Prevent instantiation
    }

    public static String getBearerToken() {
        return getRequired("cogniti.bearerToken");
    }

    private static String getRequired(String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Missing required secret: " + key
            );
        }
        return value;
    }
}
