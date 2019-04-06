package org.duangsuse.telegramscanner.model;

import org.duangsuse.telegramscanner.helper.Strings;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Telegram message with following fields:
 * <br>
 * <ul>
 *     <li>messageHeaderType
 *     <li>messageExtRef
 *     <li>messageBodyType
 *     <li>messageBody
 *     <li>links
 *     <li>hashTags
 * </ul>
 */
public class Message<T> {
    private MessageHeaderType headerType = MessageHeaderType.NORMAL;
    /**
     * Message header (name, publishedAt)
     */
    private MessageHead header = new MessageHead();
    /**
     * Extra data, like filename information included in message
     */
    private T messageExtRef;

    private MessageBodyType bodyType = MessageBodyType.NORMAL;
    /**
     * Message body string
     */
    private String messageBody = "";

    private Collection<String> links = new LinkedList<String>();
    private Collection<String> hashtags = new LinkedList<String>();

    /**
     * messageBody toString preview length
     */
    private static final int BODY_PREVIEW_LEN = 10;

    /**
     * Blank constructor
     */
    public Message() {}

    @Override
    public String toString() {
        // count links and hashtags
        final StringBuilder desc = new StringBuilder();
        if (links.size() != 0) desc.append(links.size()).append(" links");
        if (hashtags.size() != 0) desc.append(hashtags.size()).append(" tags");

        final String fmt = "Message{Hd%s, Bd%s, ext=%s}[%s](%s..., %s)";
        return String.format(fmt, headerType, bodyType, messageExtRef.toString(), header, Strings.take(BODY_PREVIEW_LEN, messageBody), desc);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message<?> message = (Message<?>) o;

        if (headerType != message.headerType) return false;
        if (!header.equals(message.header)) return false;
        if (messageExtRef != null ? !messageExtRef.equals(message.messageExtRef) : message.messageExtRef != null)
            return false;
        if (bodyType != message.bodyType) return false;
        if (!messageBody.equals(message.messageBody)) return false;
        if (!links.equals(message.links)) return false;
        return hashtags.equals(message.hashtags);
    }

    @Override
    public int hashCode() {
        int result = headerType.hashCode();
        result = 31 * result + header.hashCode();
        result = 31 * result + (messageExtRef != null ? messageExtRef.hashCode() : 0);
        result = 31 * result + bodyType.hashCode();
        result = 31 * result + messageBody.hashCode();
        result = 31 * result + links.hashCode();
        result = 31 * result + hashtags.hashCode();
        return result;
    }

    public MessageHeaderType getHeaderType() {
        return headerType;
    }

    public void setHeaderType(MessageHeaderType headerType) {
        this.headerType = headerType;
    }

    public MessageHead getHeader() {
        return header;
    }

    public void setHeader(MessageHead header) {
        this.header = header;
    }

    public T getMessageExtRef() {
        return messageExtRef;
    }

    public void setMessageExtRef(T messageExtRef) {
        this.messageExtRef = messageExtRef;
    }

    public MessageBodyType getBodyType() {
        return bodyType;
    }

    public void setBodyType(MessageBodyType bodyType) {
        this.bodyType = bodyType;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public Collection<String> getLinks() {
        return links;
    }

    public void setLinks(Collection<String> links) {
        this.links = links;
    }

    public Collection<String> getHashtags() {
        return hashtags;
    }

    public void setHashtags(Collection<String> hashtags) {
        this.hashtags = hashtags;
    }
}
