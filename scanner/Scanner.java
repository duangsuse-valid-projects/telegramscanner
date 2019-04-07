package org.duangsuse.telegramscanner.scanner;

import org.duangsuse.telegramscanner.Main;
import org.duangsuse.telegramscanner.helper.Strings;
import org.duangsuse.telegramscanner.model.*;
import org.duangsuse.telegramscanner.sourcemanager.Identifiable;
import org.duangsuse.telegramscanner.sourcemanager.SourceLocation;
import org.duangsuse.telegramscanner.sourcemanager.SourceManager;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;

/**
 * Telegram message scanner
 * <br>
 * scanner is an iterable object (stream), calling it's next method will
 * result a new message object (if successful) and move line pointer to message
 * text end (prepare to read next message) or null (if EOS occur)
 *
 * @see org.duangsuse.telegramscanner.model.MessageHead
 * @see org.duangsuse.telegramscanner.model.Message scanner stream
 *
 * @author duangsuse
 */
public class Scanner extends Utf8LineInputStream implements Iterable<Message<String>> {
    private ScannerState state = ScannerState.EXPECT_MESSAGE;

    private int offset, line, messageNo, localLine;

    private String lastLine;
    private Message<String> lastMessage;

    // used in lambda
    private MessageHead $lastHead;
    // used in lambda
    private String $extRef;
    private MessageHeaderType $lastHeadType = MessageHeaderType.NORMAL;
    private MessageBodyType $lastBodyType;
    // used in lambda
    private final StringBuffer bodyBuffer = new StringBuffer();
    private List<String> $hashtags = new LinkedList<>(), $links = new LinkedList<>(),
            $bareLinks = new LinkedList<>(), $inlineLinks = new LinkedList<>();

    private boolean keepLineOnce = false;

    public Scanner(InputStream is) {
        super(is);
    }

    /* Temporary field */
    //private String scanningHeadName;
    //private Date scanningHeadDate;

    // and tgType, tgExtRef, tgBodyType, tgBody

    @NotNull
    @Override
    public Iterator<Message<String>> iterator() {
        return new Iterator<Message<String>>() {
            @Override
            public boolean hasNext() {
                return nextLine() != null;
                // must be called before next(), moving data(line) pointer to next line
            }

            @Override
            public Message<String> next() {
                lastMessage = new Message<>();

                String tgName = "";
                Date published = null;

                // now, scan them
                switch (state) {
                    case EXPECT_MESSAGE:
                        lineDoUntil((s) -> RegexConstants.MESSAGE_HEAD.matcher(s).matches(), (m) ->
                            scannerWarn("Ignoring line " + m));
                        //scannerWarn("INFO", lastLine);
                        //now message header is in lastLine

                        // set tgName and tgHeader
                        if (lastLine == null) return null; // bad practice
                        Matcher m = RegexConstants.MESSAGE_HEAD.matcher(lastLine);
                        m.reset();
                        assert m.matches(): "Checked lastLine should be matched regex pattern";
                        if (m.matches())
                            tgName = m.group(1);

                        final String dd = m.group(2);
                        final String MM = m.group(3);
                        final String yy = m.group(4);
                        final String hh = m.group(5);
                        final String mm = m.group(6);

                        try {
                             published = new Date((Integer.parseInt(yy) + 2000) - 1900, Integer.parseInt(MM) - 1, Integer.parseInt(dd), Integer.parseInt(hh), Integer.parseInt(mm));
                        } catch (NumberFormatException e) {
                            scannerWarn("Bad number format, " + e.getMessage());
                            published = new Date();
                        }

                        scannerInfo("Begin scan header, " + String.format("name: %s, date: %s", tgName, published));
                        state = ScannerState.SCAN_HEAD;

                        /* fail through */
                    case SCAN_HEAD:
                        String finalTgName = tgName;
                        Date finalPublished = published;

                        nextLine();

                        lineDoWhile((it) -> it.startsWith("["), (line) -> {
                            // check special matches, set tgType, tgExtRef, tgBodyType
                            Matcher match;
                            MessageHeaderType type = MessageHeaderType.NORMAL;
                            String extRef = "";

                            if (RegexConstants.HEAD_FORWARD.matcher(line).matches()) {
                                match = RegexConstants.HEAD_FORWARD.matcher(line);
                                type = MessageHeaderType.FORWARDED;
                                if (match.find())
                                    extRef = match.group(1);

                            } else if (RegexConstants.HEAD_REPLY.matcher(line).matches()) {
                                match = RegexConstants.HEAD_REPLY.matcher(line);
                                type = MessageHeaderType.REPLY;
                                if (match.find())
                                    extRef = match.group(1);

                            } else if (RegexConstants.HEAD_FILE.matcher(line).matches()) {
                                match = RegexConstants.HEAD_FILE.matcher(line);
                                type = MessageHeaderType.HAS_FILE;
                                if (match.find())
                                    extRef = match.group(1);
                            } else if (RegexConstants.HEAD_STICKER.matcher(line).matches()) {
                                match = RegexConstants.HEAD_STICKER.matcher(line);
                                type = MessageHeaderType.IS_STICKER;
                                if (match.find()) /* certainly */
                                    extRef = match.group(1);
                            } else if (RegexConstants.HEAD_IS_ALBUM.matcher(line).matches()) {
                                type = MessageHeaderType.A_ALBUM;
                            } else if (RegexConstants.HEAD_IS_PHOTO.matcher(line).matches()) {
                                type = MessageHeaderType.A_PHOTO;
                            }

                            if (type == MessageHeaderType.FORWARDED)
                                $lastHead = new ForwardedMessageHead(finalTgName, finalPublished, extRef);
                            if (type == MessageHeaderType.REPLY)
                                $lastHead = new RepliedMessageHead(finalTgName, finalPublished, extRef);

                            $lastHead = new MessageHead(finalTgName, finalPublished);
                            $extRef = extRef;
                            $lastHeadType = type;
                        });

                        if ($lastHead == null)
                            $lastHead = new MessageHead(tgName, published);

                        if ($extRef == null)
                            $extRef = String.valueOf("");

                        scannerInfo("Break; Scanning message body, " + $lastHeadType + "~" + $lastHead.toString() + ", E:" + $extRef);
                        markSourceObject($lastHead);
                        state = ScannerState.SCAN_BODY;

                        /* fail through */
                    case SCAN_BODY:
                        lineDoUntil((it) -> it.endsWith("]") && RegexConstants.MESSAGE_HEAD.matcher(it).matches(), (line) -> {
                            // check label links, inline links, plain links, hashtags
                            // and read body text

                            Matcher tagsMatcher = RegexConstants.MessageBodyRegexConstants.HASHTAG.matcher(line);
                            Matcher inlineMatcher = RegexConstants.MessageBodyRegexConstants.LINK_INLINED.matcher(line);
                            Matcher bareMatcher = RegexConstants.MessageBodyRegexConstants.LINK_BARE.matcher(line);
                            Matcher markdownMatcher = RegexConstants.MessageBodyRegexConstants.LINK_TELEGRAM.matcher(line);

                            matchTextPart(tagsMatcher, $hashtags, 2);
                            matchTextPart(inlineMatcher, $inlineLinks, 1, 2);
                            matchTextPart(bareMatcher, $bareLinks, 1, 2);
                            matchTextPart(markdownMatcher, $links, 1, 2);

                            bodyBuffer.append(line).append(System.lineSeparator());
                        });
                        keepLineOnce = true; // keep message head line

                        if ($links.size() + $inlineLinks.size() + $bareLinks.size() != 0) {
                            if ($hashtags.isEmpty()) $lastBodyType = MessageBodyType.HAS_LINKS;
                            else $lastBodyType = MessageBodyType.HAS_LINKS_AND_HASTAGS;
                        } else if (!$hashtags.isEmpty())
                            $lastBodyType = MessageBodyType.HAS_HASHTAGS;
                        else $lastBodyType = MessageBodyType.NORMAL;

                        lastMessage = new Message<>($lastHead, $lastHeadType, $extRef);
                        lastMessage.setBodyType($lastBodyType);
                        lastMessage.setMessageBody(bodyBuffer.toString());

                        lastMessage.getLinks().addAll($links);
                        lastMessage.getLinks().addAll($inlineLinks);
                        lastMessage.getLinks().addAll($bareLinks);

                        lastMessage.getHashtags().addAll($hashtags);

                        $links.clear();
                        $hashtags.clear();
                        $bareLinks.clear();
                        $inlineLinks.clear();

                        // clear
                        if (bodyBuffer.length() != 0)
                            bodyBuffer.delete(0, bodyBuffer.length());

                        scannerInfo("Break; Scanning new message");
                        state = ScannerState.EXPECT_MESSAGE;
                        break;
                }

                ++messageNo;
                localLine = 0;
                markSourceObject(lastMessage);
                return lastMessage;
            }
        };
    }

    /**
     * Fetch current scanner source location
     *
     * @return current scanning location
     */
    public SourceLocation getCurrentSourceLocation() {
        return new SourceLocation(offset, line, messageNo, localLine);
    }

    /**
     * Mark source object to global {@link org.duangsuse.telegramscanner.sourcemanager.SourceManager}
     *
     * @see SourceManager#getInstance() instance pool
     * @param sourceObj identifiable object to be added with current scanner position
     */
    public void markSourceObject(Identifiable sourceObj) {
        SourceManager.getInstance().put(sourceObj, getCurrentSourceLocation());
    }

    /**
     * Match text part using {@link Matcher}, collecting groups
     *
     * @param matcher text matcher
     * @param dst destination collection
     * @param separator string join separator
     * @param groups to be collected (and concatenated)
     */
    private void matchTextPart(@NotNull Matcher matcher, Collection<String> dst, String separator, int... groups) {
        //if (matcher.matches())
        //    for (int i = 1; i < matcher.groupCount(); i++)
        //        dst.add(matcher.group(i).trim());

        while (matcher.find()) {
            StringBuilder sb = new StringBuilder();

            for (int i: groups) {
                sb.append(matcher.group(i)).append(separator);
            }

            sb.delete(sb.length() - separator.length(), sb.length());

            dst.add(sb.toString());
        }
    }

    /**
     * Match text part using {@link Matcher}, collecting groups, using "://" as separator
     *
     * @param matcher text matcher
     * @param dst destination collection
     * @param groups to be collected (and concatenated)
     */
    private void matchTextPart(@NotNull Matcher matcher, Collection<String> dst, int... groups) {
        matchTextPart(matcher, dst, "://", groups);
    }

    /**
     * messageBody toString preview length
     */
    private static final int BODY_PREVIEW_LEN = 10;

    /**
     * Call with default tag "WARN"
     *
     * @param message warning message
     */
    protected void scannerWarn(String message) { scannerLog("WARN", message); }
    /**
     * Call with tag "INFO"
     *
     * @param message warning message
     */
    protected void scannerInfo(String message) { scannerLog("INFO", message); }
    /**
     * Output a warning log message
     *
     * @param tag tag to be applied
     * @param message message to output
     */
    protected void scannerLog(final String tag, String message) {
        Main.err.print(tag);
        Main.err.print(": ");
        Main.err.print(state);
        Main.err.print("@" + offset + "(" + line + ")");
        Main.err.print(String.format(": M#%d:%d, %s...", messageNo, localLine, Strings.take(BODY_PREVIEW_LEN, lastLine)));
        Main.err.println(message);
    }

    /**
     * Do something while predicate is true
     * <br>
     * lastLine must not null before calling
     *
     * @param predicate predicate to be called with current line
     * @param action result action to be called when predicate is true
     */
    @SuppressWarnings("WeakerAccess")
    protected void lineDoWhile(Function<String, Boolean> predicate, Consumer<String> action) {
        while (lastLine != null && predicate.apply(lastLine)) {
            action.accept(lastLine);
            nextLine();
        }
    }

    /**
     * Do something while predicate is false
     * <br>
     * lastLine must not null before calling
     *
     * @param predicate predicate to be called with current line
     * @param action result action to be called when predicate is false
     */
    protected void lineDoUntil(final Function<String, Boolean> predicate, Consumer<String> action) {
        lineDoWhile((s) -> !predicate.apply(s), action);
    }

    @Override
    public String toString() {
        return String.format("TelegramScanner(F:%sS:%s,Off=%d,Line=%d,No=%d:%d@%s)",
                super.toString(), state, offset, line, messageNo, localLine, lastMessage);
    }

    /**
     * Ensure one-line-readable line stream
     *
     * @see this#lastLine "buffered" line
     * @return next line if succeeded, null otherwise
     */
    protected String nextLine() {
        ++localLine;
        try {
            if (lastLine == null)
                return readLine();

            if (!keepLineOnce) {
                readLine();
            } else keepLineOnce = false; // line kept

            return lastLine;
        } catch (IOException ignored) { return null; }
    }

    @Override
    public String readLine() throws IOException {
        String read = super.readLine();
        lastLine = read;
        line++;
        if (read != null)
            offset += read.length();
        return read;
    }

    public ScannerState getState() {
        return state;
    }

    public int getOffset() {
        return offset;
    }

    public int getLine() {
        return line;
    }

    public int getMessageNo() {
        return messageNo;
    }

    public int getLocalLine() {
        return localLine;
    }

    public Message<String> getLastMessage() {
        return lastMessage;
    }

    /**
     * Scanner state
     *
     * @see Scanner
     */
    public enum ScannerState {
        /**
         * Message reader entrance
         */
        EXPECT_MESSAGE,
        /**
         * Scanning message header (may read special tags)
         */
        SCAN_HEAD,
        /**
         * Scanning message body (may read links)
         */
        SCAN_BODY
    }
}
