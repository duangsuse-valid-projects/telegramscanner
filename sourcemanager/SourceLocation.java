package org.duangsuse.telegramscanner.sourcemanager;

/**
 * Scanner source location
 *
 * <br>
 * <ul>
 *     <li>Text offset
 *     <li>Line
 *     <li>Message No
 *     <li>Message Local line count
 * </ul>
 */
public class SourceLocation {
    private int offset, line;

    /**
     * Scanned message number index
     */
    private int messageNo;
    /**
     * Scanned at line, relative to message header start
     */
    private int messageLine;

    public SourceLocation() {
        offset = -1;
    }

    public SourceLocation(int offset, int line, int messageNo, int messageLine) {
        super();
        this.offset = offset;
        this.line = line;
        this.messageNo = messageNo;
        this.messageLine = messageLine;
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

    public int getMessageLine() {
        return messageLine;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SourceLocation that = (SourceLocation) o;

        if (offset != that.offset) return false;
        if (line != that.line) return false;
        if (messageNo != that.messageNo) return false;
        return messageLine == that.messageLine;
    }

    @Override
    public int hashCode() {
        int result = offset;
        result = 31 * result + line;
        result = 31 * result + messageNo;
        result = 31 * result + messageLine;
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SourceLocation");
        sb.append('(').append('@').append(offset).append('L').append(line)
                .append(", Message#").append(messageNo).append(':').append(messageLine).append(')');

        return sb.toString();
    }
}
