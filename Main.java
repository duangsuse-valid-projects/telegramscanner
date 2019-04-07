package org.duangsuse.telegramscanner;

import org.duangsuse.telegramscanner.scanner.Utf8LineInputStream;

import java.io.IOException;
import java.io.PrintStream;

/**
 * Application main class
 *
 * @author duangsuse
 * @version 1.0
 */
public class Main {
    private Main() {}

    /**
     * Program version name
     */
    @SuppressWarnings("WeakerAccess")
    public static final String VERSION = "1.0";

    /**
     * Standard output
     */
    private static PrintStream out = System.out;
    /**
     * Standard input
     */
    private static PrintStream err = System.err;

    /**
     * Program entrance
     * <br>
     * @param args file(path)s to be processed
     */
    public static void main(String... args) {
        err.print("TelegramScanner version "); err.println(VERSION);

        Utf8LineInputStream input = new Utf8LineInputStream(System.in);
        String line = "";

        try {
            do {
                out.print(line);
                out.println();
                out.print("> ");
            }
            while ((line = input.readLine()) != null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
