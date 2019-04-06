package org.duangsuse.telegramscanner.sourcemanager;

/**
 * Identifiable by integer
 */
public interface Identifiable {
    /**
     * Gets identical object id that is:
     * <br>
     *
     * <ul>
     *     <li>Reflexive: Object with same value have same identity</li>
     *     <li>Consistent: The identity should be match for the same object whenever this method was called</li>
     * </ul>
     *
     * @return object JVM global identity
     */
    int getIdentity();
}
