package org.duangsuse.telegramscanner.scanner;

import java.util.regex.Pattern;

/**
 * A top-level class made for RegExp constants
 * <br>
 * Used by Telegram message line scanner
 *
 * <h4>{@link Pattern} Examples</h4>
 * <blockquote><pre><code>
 * import java.util.regex.*;
 *
 * Pattern pat = Pattern.compile("(^|\\s)#(?!#)((\\S(?&lt;![\\(\\)]))+)");
 * Matcher m = pat.matcher("#abc #dev");
 * m.reset();
 *
 * while (m.find()) { println(m.group().trim()); }
 * </code></pre></blockquote>
 *
 * <br>
 * <h4><a name ="regexp_usage">Regular expression usage</a></h4>
 * <table border="3" cellpadding="1" cellspacing="4" summary="Regexp usage">
 *     <thead>
 *         <tr align="left">
 *             <th>Name</th>
 *             <th>Matches</th>
 *         </tr>
 *     </thead>
 *     <tbody>
 *         <tr>
 *             <td>Message Head</td>
 *             <td>name, [dd.MM.yy hh.mm]</td>
 *         </tr>
 *         <tr>
 *             <td>Head Reply</td>
 *             <td>In reply to <i>name</i></td>
 *         </tr>
 *         <tr>
 *             <td>Head Forward</td>
 *             <td>Forwarded from <i>name</i></td>
 *         </tr>
 *         <tr>
 *             <td>Is Album / Photo</td>
 *             <td>(The message containing a photo or message)</td>
 *         </tr>
 *         <tr>
 *             <td>Sticker</td>
 *             <td><i>?</i> Sticker</td>
 *         </tr>
 *         <tr>
 *             <td>File</td>
 *             <td>File : <i>filename</i></td>
 *         </tr>
 *     </tbody>
 * </table>
 *
 * @see Pattern#compile(String) Regexp API used
 */
public final class RegexConstants {
    /**
     * Make a new pattern using code string
     *
     * @param regex regex expression string, should not be null
     * @return compiled regexp {@link Pattern#compile(String)}
     */
    private static @org.jetbrains.annotations.NotNull Pattern $(@org.jetbrains.annotations.NotNull String regex) { return Pattern.compile(regex); }

    /**
     * New message header definition<br>
     * in form <code>(name), [dd.mm.yy hh:mm]</code>
     * <p><br>
     * <sub>Examples</sub>
     * <ul>
     *     <li>duangsuse::Echo, [24.03.19 11:22]</li>
     *     <li>name, [dd.MM.yy hh.mm]</li>
     * </ul>
     */
    public static final Pattern MESSAGE_HEAD = $("^(.+), \\[(\\d{2})\\.(\\d{2})\\.(\\d{2}) (\\d{2}):(\\d{2})\\]$");

    /**
     * Reply-to message header definition
     * <p><br>
     * <sub>Examples</sub>
     * <ul>
     *     <li>[In reply to duangsuse::Echo]</li>
     * </ul>
     */
    public static final Pattern HEAD_REPLY = $("^\\[In reply to (.+)\\]$");
    /**
     * Forwarded from <i>(display name)</i>
     * <p><br>
     * <sub>Examples</sub>
     * <ul>
     *      <li>[Forwarded from 羽毛的小白板]</li>
     * </ul>
     */
    public static final Pattern HEAD_FORWARD = $("^\\[Forwarded from (.+)\\]$");


    /**
     * An uploaded telegram file <i>(.+)</i>
     *
     * <p><br>
     * <sub>Examples</sub>
     * <ul>
     *     <li>[ File : AndroidManifest.xml ]</li>
     * </ul>
     */
    public static final Pattern HEAD_FILE = $("^\\[ File : (.+) \\]$");
    /**
     * Sticker picture of character <i>(.)</i>
     *
     * <p><br>
     * <sub>Examples</sub>
     * <ul>
     *     <li>[ 😋 Sticker ]</li>
     * </ul>
     */
    public static final Pattern HEAD_STICKER = $("^\\[ (.) Sticker \\]$");


    /**
     * Indicates that this message contains a photo collection
     */
    public static final Pattern HEAD_IS_ALBUM = $("^\\[ Album \\]$");
    /**
     * Indicates that this message contains a photo
     */
    public static final Pattern HEAD_IS_PHOTO = $("^\\[ Photo \\]$");


    /**
     * A static class for Links and Hash-tags scanner Regex in message body
     * <br>
     * <h4><a name ="regexp_usage">Regular expression usage</a></h4>
     * <table border="3" cellpadding="1" cellspacing="4" summary="Regexp usage">
     *     <thead>
     *         <tr align="left">
     *             <th>Name</th>
     *             <th>Matches</th>
     *         </tr>
     *     </thead>
     *     <tbody>
     *         <!-- Links --!>
     *         <tr>
     *             <td>Bare link</td>
     *             <td>(newline)<i>URL</i> ...text...</td>
     *         </tr>
     *         <tr>
     *             <td>Telegram link</td>
     *             <td>...text... (<i>URL</i>) ...text...</td>
     *         </tr>
     *         <tr>
     *             <td>Inlined links</td>
     *             <td>...text... <i>URL</i> ...text...</td>
     *         </tr>
     *         <tr>
     *             <td>Hash-tag</td>
     *             <td>...text... #<i>tag</i> ...text...</td>
     *         </tr>
     *     </tbody>
     * </table>
     */
    public static final class MessageBodyRegexConstants {
        /**
         * Telegram topic Hash-tags
         * <p><br>
         * <sub>Examples</sub>
         *
         * <ul>
         *    <li>Telegram #hashtag
         *    <li>#Topic_misc a new topic
         *    <li>#offtopic
         *    <li>#java 11 released!
         * </ul>
         */
        public static final Pattern HASHTAG = $("(^|\\s)#(?!#)((\\S(?<![\\(\\)]))+)");
        /**
         * Telegram anchor tags
         * <br>
         * Form: <i>text (url)</i>
         *
         * <p><br>
         * <sub>Examples</sub>
         * <ul>
         *     <li>系统服务 (https://blog.yuuta.moe/2017/11/10/from-vibrator-to-system-service/)</li>
         * </ul>
         */
        public static final Pattern LINK_TELEGRAM = $("\\((\\w+)://(\\S+)\\)");
        /**
         * User inline text URL links
         * <p><br>
         * <sub>Examples</sub>
         * <ul>
         *      <li>See: https://github.com/duangsuse/RandomPicture/commit/440b8a1c7d2251b0074c1571c0d07c613628fc54 <3</li>
         * </ul>
         */
        public static final Pattern LINK_INLINED = $("(?![\\(\\)]).((http|https):(\\S+))");
        /**
         * Bare text links, a newline starting with http|https
         * <p><br>
         * <sub>Examples</sub>
         *
         * <ul>
         *     <li>https://github.com/aosp-mirror/platform_frameworks_base/blob/pie-release/tools/aapt2</li>
         *     <li>http://localhost:8080</li>
         * </ul>
         */
        public static final Pattern LINK_BARE = $("^(http|https):(\\S+)");
    }
}
