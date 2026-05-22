/**
 * Module descriptor for the Tasktopia application.
 *
 * <h2>Dependencies</h2>
 * <ul>
 *   <li>{@code javafx.controls} — JavaFX UI controls (buttons, labels, lists, etc.)</li>
 *   <li>{@code javafx.fxml}     — FXML loader and controller injection</li>
 *   <li>{@code java.sql}        — JDBC database access</li>
 *   <li>{@code com.google.gson} — JSON serialisation / deserialisation via Gson</li>
 *   <li>{@code java.net.http}   — Java HTTP client used for AI API calls</li>
 * </ul>
 *
 * <h2>Opened packages</h2>
 * <ul>
 *   <li>{@code com.tasktopia}, {@code com.tasktopia.controller},
 *       {@code com.tasktopia.model}, and {@code fxml} are opened to
 *       {@code javafx.fxml} to allow reflective FXML controller instantiation.</li>
 *   <li>{@code au.edu.qut} and {@code au.edu.qut.cogniti} are opened to
 *       {@code com.google.gson} to allow reflective JSON mapping.</li>
 *   <li>{@code ai} is opened to {@code com.google.gson} for the same reason.</li>
 * </ul>
 *
 * <h2>Exported packages</h2>
 * <p>All primary application packages are exported for use by other modules
 * on the module path.</p>
 */
module com.tasktopia {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.google.gson;
    requires java.net.http;

    opens com.tasktopia to javafx.fxml;

    opens com.tasktopia.controller to javafx.fxml;
    opens com.tasktopia.model to javafx.fxml;
    opens fxml to javafx.fxml;

    opens au.edu.qut to com.google.gson;
    opens au.edu.qut.cogniti to com.google.gson;

    exports com.tasktopia;
    exports com.tasktopia.controller;
    exports com.tasktopia.model;
    exports com.tasktopia.util;

    exports au.edu.qut;
    exports au.edu.qut.cogniti;

    opens ai to com.google.gson;
}