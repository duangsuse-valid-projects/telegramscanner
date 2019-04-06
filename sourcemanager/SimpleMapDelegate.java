package org.duangsuse.telegramscanner.sourcemanager;

import org.jetbrains.annotations.Contract;

import java.util.HashMap;
import java.util.Set;

/**
 * Delegates {@link HashMap}, copying some useful method
 * <br>
 * <ul>
 *     <li>size
 *     <li>get/put
 *     <li>containsKey
 *     <li>remove
 *     <li>clone
 *     <li>equals/hashcode/toString
 * </ul>
 * @param <K> Key type
 * @param <V> Value type
 * @see HashMap delegated class
 */
@SuppressWarnings("SuspiciousMethodCalls") /* unnecessary for delegates */
public class SimpleMapDelegate<K, V> {
    private HashMap<K, V> mMap;

    @SuppressWarnings("WeakerAccess") /* should be shared */
    protected SimpleMapDelegate(HashMap<K, V> receiver) {
        this.mMap = receiver;
    }

    public int size() {
        return mMap.size();
    }

    public V get(Object key) {
        return mMap.get(key);
    }

    public boolean containsKey(Object key) {
        return mMap.containsKey(key);
    }

    public V put(K key, V value) {
        return mMap.put(key, value);
    }

    public V remove(Object key) {
        return mMap.remove(key);
    }

    public Set<K> keySet() {
        return mMap.keySet();
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleMapDelegate<?, ?> that = (SimpleMapDelegate<?, ?>) o;

        return mMap.equals(that.mMap);
    }

    @Override
    public int hashCode() {
        return mMap.hashCode();
    }

    @Override
    public String toString() {
        return mMap.toString();
    }
}
