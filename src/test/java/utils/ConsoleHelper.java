package utils;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import java.awt.GraphicsEnvironment;
import java.util.Scanner;

/**
 * Interactive reader used when the test needs the user to paste in a value
 * mid-run (e.g. the password-reset link that arrives in Gmail).
 *
 * <p>A pop-up dialog is used by default because Gradle does not forward console
 * stdin to the forked test JVM (a bare {@link Scanner} on System.in throws
 * "No line found" immediately). The dialog is kept always-on-top so it stays
 * visible while the user switches to the browser/Gmail to copy the link.
 * When running headless, it falls back to stdin.
 */
public final class ConsoleHelper {

    private static final Scanner SCANNER = new Scanner(System.in);

    private ConsoleHelper() {}

    public static String prompt(String message) {
        // Preferred path: a blocking, always-on-top GUI dialog. Always-on-top keeps
        // it floating above Chrome so switching windows to copy the link does not
        // hide or dismiss it.
        if (!GraphicsEnvironment.isHeadless()) {
            JOptionPane pane = new JOptionPane(
                message, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
            pane.setWantsInput(true);

            JDialog dialog = pane.createDialog(null, "Action required");
            dialog.setAlwaysOnTop(true);
            dialog.setVisible(true);   // blocks until OK/Cancel
            dialog.dispose();

            Object value = pane.getInputValue();
            return value == JOptionPane.UNINITIALIZED_VALUE || value == null
                ? "" : value.toString().trim();
        }

        // Headless fallback: read from stdin (requires a console attached).
        System.out.println();
        System.out.println("==================================================");
        System.out.println(">>> " + message);
        System.out.println("==================================================");
        System.out.print("Input: ");
        return SCANNER.nextLine().trim();
    }

    public static String promptForResetLink() {
        return prompt(
            "Open the Gmail account you just registered with, find the password " +
            "reset email, and paste the full reset link here, then click OK."
        );
    }
}
