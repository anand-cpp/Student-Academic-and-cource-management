package db;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import util.AppException;

/**
 * Centralized database connectivity.
 *
 * <p>This is the single place where a JDBC {@link Connection} is created. All
 * DAO classes obtain connections from here, which avoids opening and closing
 * physical connections repeatedly and keeps credentials and the URL in one
 * configurable location.
 *
 * <h2>Configuration</h2>
 * <p>The driver reads {@code config/db.properties} from the working directory.
 * If that file is missing, sensible defaults are used. Never commit real
 * passwords to source control — use {@code config/db.properties.example} as a
 * template and create your own {@code config/db.properties} locally.
 *
 * <h2>Connection handling</h2>
 * <p>A single connection is reused and validated before each use. If it is
 * closed or invalid (for example after a MySQL restart), a fresh connection is
 * established automatically. Callers should still use try-with-resources for
 * {@link PreparedStatement} and {@link ResultSet}; the shared Connection itself
 * is managed here.
 */
public class DatabaseConnection {

    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/student_academic_management"
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";

    private static final String PROPERTIES_FILE = "config/db.properties";

    private DatabaseConnection() {
        // Utility class - not instantiable
    }

    /**
     * Open a new connection. Callers own the connection and must close it.
     *
     * <p>Each call returns an independent connection so background
     * {@code SwingWorker} tasks can query the database concurrently without
     * sharing a non-thread-safe JDBC connection. DAOs close connections with
     * try-with-resources; services close them after committing a transaction.
     *
     * @return an open JDBC connection
     * @throws AppException if the driver cannot be loaded or the server cannot
     *                      be reached
     */
    public static Connection getConnection() throws AppException {
        return openNewConnection();
    }

    private static Connection openNewConnection() throws AppException {
        Properties props = loadConfig();
        String url = props.getProperty("db.url", DEFAULT_URL);
        String user = props.getProperty("db.user", DEFAULT_USER);
        String password = props.getProperty("db.password", DEFAULT_PASSWORD);

        try {
            // Ensure the JDBC driver is registered (harmless on modern JDBC,
            // but explicit for clarity with older driver versions).
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            throw new AppException("DRIVER_NOT_FOUND",
                    "MySQL JDBC driver not found on the classpath. Add "
                    + "lib/mysql-connector-j-8.3.0.jar (or the driver you downloaded) "
                    + "to the classpath when compiling and running.", ex);
        }

        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException ex) {
            throw new AppException("DB_CONNECTION_FAILED",
                    "Cannot connect to MySQL. Check that the MySQL server is running, "
                    + "the database '" + extractDatabase(url) + "' exists, the credentials in "
                    + PROPERTIES_FILE + " are correct, and the JDBC driver JAR is on the classpath.",
                    ex);
        }
    }

    private static String extractDatabase(String url) {
        int i = url.lastIndexOf('/');
        int j = url.indexOf('?', i);
        if (j == -1) {
            j = url.length();
        }
        return (i >= 0 && j > i) ? url.substring(i + 1, j) : "<unknown>";
    }

    private static Properties loadConfig() {
        Properties props = new Properties();
        // Load defaults first
        props.setProperty("db.url", DEFAULT_URL);
        props.setProperty("db.user", DEFAULT_USER);
        props.setProperty("db.password", DEFAULT_PASSWORD);

        Path path = Paths.get(PROPERTIES_FILE);
        if (Files.isRegularFile(path)) {
            try (InputStream in = Files.newInputStream(path)) {
                props.load(in);
            } catch (IOException ex) {
                // Non-fatal: use defaults
                System.err.println("Warning: could not read " + PROPERTIES_FILE + ": " + ex.getMessage());
            }
        }
        return props;
    }

    /** Compatibility hook. Connections are now owned by their callers. */
    public static void close() {
        // No shared connection remains to close.
    }
}
