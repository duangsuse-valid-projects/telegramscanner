package org.duangsuse.telegramscanner.model;

/**
 * Special message body type
 */
public enum MessageBodyType {
    /**
     * Normal text message
     */
    NORMAL,

    /**
     * Has links
     */
    HAS_LINKS,

    /**
     * Has hashtags
     */
    HAS_HASHTAGS,

    /**
     * Has links and hashtags
     */
    HAS_LINKS_AND_HASTAGS
}
