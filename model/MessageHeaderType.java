package org.duangsuse.telegramscanner.model;

/**
 * Telegram Message header type
 */
public enum MessageHeaderType {
    /**
     * Normal head
     */
    NORMAL,

    /**
     * Reply to message
     */
    RELPY,
    /**
     * Forwarded message
     */
    FORWARDED,

    /**
     * Photo
     */
    A_PHOTO,
    /**
     * Album
     */
    A_ALBUM,

    /**
     * Has a file
     */
    HAS_FILE,
    /**
     * Is a sticker
     */
    IS_STICKER
}
