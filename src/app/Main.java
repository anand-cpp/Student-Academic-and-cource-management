package app;

import ui.MainFrame;
import javax.swing.SwingUtilities;

/**
 * Application entry point.
 *
 * <p>Launches the Student Academic &amp; Course Management System desktop
 * application (Swing UI + JDBC persistence to MySQL).
 *
 * <p>Compile and run (from the project root, with the JDBC driver JAR in
 * lib/):
 *
 *   javac -d build -cp "lib/mysql-connector-j-8.3.0.jar" $(find src -name "*.java")
 *   java -cp "build;lib/mysql-connector-j-8.3.0.jar" app.Main
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
