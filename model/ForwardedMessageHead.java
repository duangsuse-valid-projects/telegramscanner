package org.duangsuse.telegramscanner.model;

import org.jetbrains.annotations.Contract;

import java.util.Date;

/**
 * Telegram forwarded-from message header
 *
 * @see MessageHead it's prototype
 */
public class ForwardedMessageHead extends MessageHead {
    /**
     * forwarded from telegram id
     */
    private String forwardedFrom;

    public ForwardedMessageHead(String name, Date date, String from) {
        super(name, date);
        this.forwardedFrom = from;
    }

    @Override
    public String toString() {
        return "ForwardedMessageHead{" + super.toString() + '}' +
                "(origin '" + forwardedFrom + '\'' + ')';
    }

    public String getForwardedFrom() {
        return forwardedFrom;
    }

    public void setForwardedFrom(String forwardedFrom) {
        this.forwardedFrom = forwardedFrom;
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ForwardedMessageHead that = (ForwardedMessageHead) o;

        return forwardedFrom.equals(that.forwardedFrom);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + forwardedFrom.hashCode();
        return result;
    }
}
