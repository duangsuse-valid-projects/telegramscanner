package org.duangsuse.telegramscanner.scanner;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Line based input stream
 *
 * @see java.io.DataInput
 * @see java.io.InputStream
 */
public class Utf8LineInputStream implements Closeable {
    /**
     * Delegated data input stream
     */
    private DataInput target;

    /**
     * Construct using data input {@link DataInputStream}
     *
     * @param dataIn data input instance
     */
    public Utf8LineInputStream(DataInput dataIn) {
        this.target = dataIn;
    }

    /**
     * Construct using byte input stream
     *
     * @see InputStreamReader with utf-8 decoder support
     * @param in line-based input stream
     */
    public Utf8LineInputStream(InputStream in) {
        final InputStreamReader utf8InputReader = new InputStreamReader(in, UTF_8);
        InputStream is = new InputStream() {
            @Override
            public int read() throws IOException {
                return utf8InputReader.read();
            } /* readLine() uses this method only */
        };

        this.target = new DataInputStream(is);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Utf8LineInputStream that = (Utf8LineInputStream) o;

        return target.equals(that.target);
    }

    /**
     * Utf-8 charset
     */
    @SuppressWarnings("WeakerAccess")
    protected static final Charset UTF_8 = Charset.forName("UTF-8");

    /**
     * Reads the next line of text from the input stream.
     * It reads successive bytes, converting
     * each byte separately into a character,
     * until it encounters a line terminator or
     * end of
     * file; the characters read are then
     * returned as a {@code String}. Note
     * that because this
     * method processes bytes,
     * <b>it does not support input of the full Unicode
     * character set.</b>
     * <p>
     * If end of file is encountered
     * before even one byte can be read, then {@code null}
     * is returned. Otherwise, each byte that is
     * read is converted to type {@code char}
     * by zero-extension. If the character {@code '\n'}
     * is encountered, it is discarded and reading
     * ceases. If the character {@code '\r'}
     * is encountered, it is discarded and, if
     * the following byte converts &#32;to the
     * character {@code '\n'}, then that is
     * discarded also; reading then ceases. If
     * end of file is encountered before either
     * of the characters {@code '\n'} and
     * {@code '\r'} is encountered, reading
     * ceases. Once reading has ceased, a {@code String}
     * is returned that contains all the characters
     * read and not discarded, taken in order.
     * Note that every character in this string
     * will have a value less than {@code \u005Cu0100},
     * that is, {@code (char)256}.
     *
     * @return the next line of text from the input stream,
     *         or {@code null} if the end of file is
     *         encountered before a byte can be read.
     * @exception  IOException  if an I/O error occurs.
     */
    public String readLine() throws IOException {
        return target.readLine();
    }

    @Override
    public void close() throws IOException {
        if (target instanceof Closeable)
            ((Closeable) target).close();
    }

    @Override
    public String toString() {
        return "Utf8LineInputStream(" + target.toString() + ")";
    }
}
