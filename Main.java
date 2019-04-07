package org.duangsuse.telegramscanner;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import com.sun.webkit.dom.HTMLDocumentImpl;
import org.duangsuse.telegramscanner.helper.YamlDump;
import org.duangsuse.telegramscanner.model.Message;
import org.duangsuse.telegramscanner.scanner.Scanner;
import org.duangsuse.telegramscanner.scanner.Utf8LineInputStream;
import org.duangsuse.telegramscanner.sourcemanager.Identifiable;
import org.duangsuse.telegramscanner.sourcemanager.SourceManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.beans.XMLEncoder;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.*;

import java.util.logging.XMLFormatter;

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
        HashSet<Message<String>> messageSet = new HashSet<>();

        if (argList.contains("-test")) {
            testInput();
            System.exit(0);
        }

        for (Message<String> stringMessage : new Scanner(System.in)) {
            messageSet.add(stringMessage);
        }

        if (argList.contains("-dump")) {
            //XMLEncoder coder = new XMLEncoder(out);

            //coder.writeObject(YamlDump.getMessageMaps(messageSet));
            //coder.flush();

            List<Map<String, Object>> maps = YamlDump.getMessageMaps(messageSet);

            Document doc = null;
            try {
                doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }

            if (doc == null) System.exit(1);

            Element list = doc.createElement("ol");
            for (Map<String, Object> map : maps) {
                Element child = doc.createElement("dl");

                for (String key : map.keySet()) {
                    Element messageObjectKey = doc.createElement("dt");
                    Element messageObject = doc.createElement("dd");

                    messageObjectKey.setTextContent(key);
                    messageObject.setTextContent(map.get(key).toString());

                    child.appendChild(messageObjectKey);
                    child.appendChild(messageObject);
                }

                list.appendChild(child);
            }

            OutputFormat fmt = new OutputFormat(doc);

            fmt.setIndenting(true);
            fmt.setIndent(4);
            fmt.setLineWidth(65);

            StringWriter writer = new StringWriter();
            XMLSerializer serializer = new XMLSerializer(writer, fmt);

            try {
                serializer.serialize(list);
            } catch (IOException e) {
                e.printStackTrace();
            }
            out.println(writer);

            System.exit(0);
        }

        if (argList.contains("-dump-yaml")) {
            YamlDump.dump(messageSet);
            System.exit(0);
        }

        if (argList.contains("-dump-debug-yaml")) {
            YamlDump.dumpSourceManager(SourceManager.getInstance());
            System.exit(0);
        }

        out.println(messageSet);

        for (Identifiable key : SourceManager.getInstance().keySet()) {
            out.print(key); out.print(": ");
            out.println(SourceManager.getInstance().get(key));

            for (Message<String> m: messageSet) {
                if (m.getIdentity() == key.getIdentity())
                    out.println(m);
            }
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
