package org.duangsuse.telegramscanner.sourcemanager;

import java.util.HashMap;

/**
 * Source line manager
 *
 * @see SimpleMapDelegate api class
 */
public class SourceManager extends SimpleMapDelegate<Identifiable, SourceLocation> {
    @SuppressWarnings("WeakerAccess") /* should be shared api */
    public SourceManager() {
        super(new HashMap<>());
    }

    /**
     * Lazy source manager singleton instance
     */
    private static final class LazyHolder {
        static final SourceManager INSTANCE = new SourceManager();
    }

    public SourceManager getInstance() { return LazyHolder.INSTANCE; }
}
