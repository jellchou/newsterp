/******************************************************************************
 * Copyright (c) 2007 Mark Alan Finlayson
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT Java Wordnet Interface 
 * Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.mit.edu/~markaf/projects/wordnet/license.html.
 *****************************************************************************/

package edu.mit.jwi.content.parse;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Objects that implement this interface are used to parse lines of data from
 * data resource into data structures that are then manipulated by the
 * dictionary.
 * 
 * @author Mark A. Finlayson
 * @version 1.00, 04/15/06
 * @since 1.5.0
 */
public abstract class AbstractCachingParser<T> implements ILineParser<T> {

    /**
     * Default initial capacity, set arbitrarily to 500.
     */
    private static final int DEFAULT_CAPACITY = 500;

    /**
     * Flag that indicates whether caching is enabled
     */
    private boolean fCachingEnabled;

    /**
     * Value that indicates the cache's initial capacity
     */
    private int fInitialCapacity;

    /**
     * The cache itself.
     */
    private Map<String, T> fCache;

    /**
     * Constructs a caching parser with caching enabled and the default initial
     * capacity.
     */
    public AbstractCachingParser() {
        this(true, DEFAULT_CAPACITY);
    }

    /**
     * Allows the user to specify if caching is initially enabled.
     */
    public AbstractCachingParser(boolean cachingEnabled) {
        this(cachingEnabled, DEFAULT_CAPACITY);
    }

    /**
     * Allows the user to specify if caching is initially enabled.
     */
    public AbstractCachingParser(int initialCapacity) {
        this(true, initialCapacity);
    }

    /**
     * Allows the user to specify if caching is initially enabled and the
     * initial capacity of the cache.
     */
    public AbstractCachingParser(boolean cachingEnabled, int initialCapacity) {
        setInitialCapacity(initialCapacity);
        setCachingEnabled(cachingEnabled);
    }

    /**
     * @return whether caching is enabled for this parser
     */
    public boolean isCachingEnabled() {
        return fCachingEnabled;
    }

    /**
     * Sets caching for this parser. Initializes cache if required.
     * 
     * @param cachingEnabled
     */
    public void setCachingEnabled(boolean cachingEnabled) {
        fCachingEnabled = cachingEnabled;
        if (fCache == null & fCachingEnabled)
            initializeCache(getInitialCapacity());
    }

    /**
     * @return the initial capacity of the cache
     */
    public int getInitialCapacity() {
        return fInitialCapacity;
    }

    /**
     * Sets the initial capacity of the cache. This number controls how many
     * objects can be added to the cache before it has to grow automatically. If
     * the cache has already been initialized, this method has no effect until
     * the cache is released ({@link #releaseCache()}) and re-initialized ({@link #initializeCache(int)}).
     * 
     * @param capacity
     *            The initial capacity of the cache.
     */
    public void setInitialCapacity(int capacity) {
        fInitialCapacity = capacity;
    }

    /**
     * Clears the cache.
     */
    public void clear() {
        if (fCache != null)
            fCache.clear();
    }

    /**
     * Initializes the cache. For internal setup. This method does not need to
     * be called by subclasses, as it is automatically called by this class's
     * constructor via the {@link #setCachingEnabled(boolean)} method.
     * 
     * @param capacity
     *            the initial capacity of the cache.
     */
    protected void initializeCache(int capacity) {
        fCache = new WeakHashMap<String, T>(capacity);
    }

    /**
     * Releases the cache. For internal cleanup if required.
     */
    protected void releaseCache() {
        fCache = null;
    }

    /**
     * Returns the item cached for the specified line, if any.
     * 
     * @param line
     *            the line for which an item is requested. If caching is
     *            disabled, this method returns <tt>null</tt>. This method is
     *            intended to be used by subclasses.
     * @return The item, if found; if not found or caching is disabled, returns
     *         <tt>null</tt>
     */
    protected T getCachedItem(String line) {
        if (!isCachingEnabled() | line == null | fCache == null)
            return null;
        return fCache.get(line);
    }

    /**
     * Caches the particular data structure for the indicated line. This means
     * that this line was parsed to the specified item. If caching is disabled,
     * this method does nothing. This method is intended to be used by
     * subclasses.
     * 
     * @param line
     *            the line that was parsed
     * @param item
     *            the item that parsing the line produced
     */
    protected void cacheItem(String line, T item) {
        if(!isCachingEnabled()) return;
        if (line != null & item != null & fCache != null) {
            fCache.put(line, item);
        }
    }

}
