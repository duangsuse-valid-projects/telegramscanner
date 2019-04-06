package org.duangsuse.telegramscanner.model;

import org.duangsuse.telegramscanner.sourcemanager.Identifiable;
import org.jetbrains.annotations.Contract;

import java.util.Date;

/**
 * Telegram message header object
 */
public class MessageHead implements Identifiable {
    /**
     * Message origin name
     */
    private String sourceName;
    /**
     * Message publication date
     */
    private Date publishedAt;

    MessageHead() {}
    public MessageHead(String name, Date date) {
        sourceName = name;
        publishedAt = date;
    }

    public Date getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Date publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    @Override
    public String toString() {
        return "MessageHead(" + sourceName + "@" + publishedAt + ')';
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MessageHead that = (MessageHead) o;

        if (!sourceName.equals(that.sourceName)) return false;
        return publishedAt.equals(that.publishedAt);
    }

    @Override
    public int hashCode() {
        int result = sourceName.hashCode();
        result = 31 * result + publishedAt.hashCode();
        return result;
    }

    @Override
    public int getIdentity() {
        return System.identityHashCode(this);
    }
}
