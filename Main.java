package org.duangsuse.telegramscanner;

import org.duangsuse.telegramscanner.model.Message;
import org.duangsuse.telegramscanner.scanner.Scanner;
import org.duangsuse.telegramscanner.scanner.Utf8LineInputStream;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

import java.util.List;

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
    public static PrintStream err = System.err;

    /**
     * Program entrance
     * <br>
     * @param args file(path)s to be processed
     */
    public static void main(String... args) {
        err.print("TelegramScanner version "); err.println(VERSION);
        List<String> argList = Arrays.asList(args);

        if (argList.contains("-test"))
            testInput();

        for (Message<String> stringMessage : new Scanner(System.in)) {
            out.print(stringMessage.toString());
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static void testInput() {
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
