package org.duangsuse.telegramscanner.model;

import org.jetbrains.annotations.Contract;

import java.util.Date;

/**
 * Telegram reply-to message header
 *
 * @see MessageHead it's prototype
 */
public class RepliedMessageHead extends MessageHead {
    /**
     * Replied to telegram id
     */
    private String repliedTo;

    public RepliedMessageHead(String name, Date date, String repliedTo) {
        super(name, date);
        this.repliedTo = repliedTo;
    }

    public String getRepliedTo() {
        return repliedTo;
    }

    public void setRepliedTo(String repliedTo) {
        this.repliedTo = repliedTo;
    }

    @Override
    public String toString() {
        return "RepliedMessageHead{" + super.toString() + '}' +
                "(replies '" + repliedTo + '\'' + ')';
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        RepliedMessageHead that = (RepliedMessageHead) o;

        return repliedTo.equals(that.repliedTo);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + repliedTo.hashCode();
        return result;
    }
}
