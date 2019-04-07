package org.duangsuse.telegramscanner.helper;

import org.duangsuse.telegramscanner.model.Message;
import org.duangsuse.telegramscanner.sourcemanager.Identifiable;
import org.duangsuse.telegramscanner.sourcemanager.SimpleMapDelegate;
import org.duangsuse.telegramscanner.sourcemanager.SourceLocation;
import org.duangsuse.telegramscanner.sourcemanager.SourceManager;
import org.jetbrains.annotations.NotNull;
import org.snakeyaml.engine.v1.api.Dump;
import org.snakeyaml.engine.v1.api.DumpSettings;
import org.snakeyaml.engine.v1.api.DumpSettingsBuilder;
import org.snakeyaml.engine.v1.common.ScalarStyle;

import java.util.*;

/**
 * Yaml dump for messages
 *
 * @see org.duangsuse.telegramscanner.model.Message
 */
public final class YamlDump {
    /**
     * Dump messages method
     */
    public static void dump(Set<Message<String>> msgs) {
        DumpSettings settings = new DumpSettingsBuilder()
                .setIndent(2)
                .setCanonical(true)
                .setSplitLines(false)
                .setDefaultScalarStyle(ScalarStyle.DOUBLE_QUOTED)
                .build();

        Dump d = new Dump(settings);

        List<Map<String, Object>> listMsgs = getMessageMaps(msgs);

        System.out.print(d.dumpToString(listMsgs));
    }

    /**
     * Convert a message set to standard collection data set
     * <br>
     * no deep copy for {@link SourceManager} required, debug information is inlined into output map
     *
     * @param msgs messages to convert
     * @return standard (list, map, int, string) representation of message set
     */
    @NotNull
    public static List<Map<String, Object>> getMessageMaps(@NotNull Set<Message<String>> msgs) {
        Map<Integer, Map<String, Integer>> debugs = getIntegerDebugMap(SourceManager.getInstance());

        List<Map<String, Object>> listMsgs = new LinkedList<>();

        for (Message<String> m : msgs) {
            Map<String, Object> yamlObject = new HashMap<>();

            yamlObject.put("header_type", m.getHeaderType().name());
            yamlObject.put("name", m.getHeader().getSourceName());
            yamlObject.put("published", m.getHeader().getPublishedAt().getTime());
            yamlObject.put("ext", m.getMessageExtRef());
            yamlObject.put("body_type", m.getBodyType().name());
            yamlObject.put("body", m.getMessageBody());
            yamlObject.put("links", m.getLinks());
            yamlObject.put("hashtags", m.getHashtags());

            if (debugs.containsKey(m.getIdentity()))
                yamlObject.put("debug", debugs.get(m.getIdentity()));

            listMsgs.add(yamlObject);
        }
        return listMsgs;
    }

    /**
     * Dump source manager
     */
    public static void dumpSourceManager(@NotNull SimpleMapDelegate<Identifiable, SourceLocation> debug) {
        Dump d = new Dump(new DumpSettingsBuilder().setIndent(2).setSplitLines(true).build());

        Map<Integer, Map<String, Integer>> repr = getIntegerDebugMap(debug);

        System.out.println(d.dumpToString(repr));
    }

    /**
     * Get json-map style debug information
     *
     * @param debug source manager map
     * @return map and sub-map representation of {@link org.duangsuse.telegramscanner.sourcemanager.SourceManager}
     */
    @NotNull
    private static Map<Integer, Map<String, Integer>> getIntegerDebugMap(@NotNull SimpleMapDelegate<Identifiable, SourceLocation> debug) {
        Map<Integer, Map<String, Integer>> repr = new HashMap<>();

        for (Identifiable key : debug.keySet()) {
            SourceLocation value = debug.get(key);

            Map<String, Integer> obj = new HashMap<>();

            obj.put("message_no", value.getMessageNo());
            obj.put("message_line", value.getMessageLine());

            obj.put("offset", value.getOffset());
            obj.put("line", value.getLine());

            repr.put(key.getIdentity(), obj);
        }
        return repr;
    }
}
