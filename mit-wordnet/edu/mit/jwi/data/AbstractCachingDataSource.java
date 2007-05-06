/******************************************************************************
 * Copyright (c) 2007 Mark Alan Finlayson
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT Java Wordnet Interface 
 * Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.mit.edu/~markaf/projects/wordnet/license.html.
 *****************************************************************************/

package edu.mit.jwi.data;

import java.util.HashMap;
import java.util.Map;

/**
 * An abstract adapter on <tt>IDictionaryDataSource</tt> that adds the ability
 * to cache lines.
 * 
 * @author Mark A. Finlayson
 * @version 1.00, 4/20/06
 * @since 1.5.0
 */
public abstract class AbstractCachingDataSource implements
        IDictionaryDataSource {

    private static final int DEFAULT_CAPACITY = 500;

    private boolean fCachingEnabled;
    private int fInitialCapacity;
    private Map<String, String> fCache;

    /**
     * Constructs the object with caching enabled and a default cache size.
     */
    public AbstractCachingDataSource() {
        this(DEFAULT_CAPACITY, true);
    }

    /**
     * Allows user to specify if caching is initially enabled.
     */
    public AbstractCachingDataSource(boolean cachingEnabled) {
        this(DEFAULT_CAPACITY, cachingEnabled);
    }

    /**
     * Allows instantiator to specify the initial cache size.
     */
    public AbstractCachingDataSource(int initialCapacity) {
        this(initialCapacity, true);
    }

    /**
     * Allows instantiator to specify both initial cache size and whether
     * caching is initially enabled.
     */
    public AbstractCachingDataSource(int initialCapacity, boolean cachingEnabled) {
        setInitialCapacity(initialCapacity);
        setCachingEnabled(cachingEnabled);
    }

    /**
     * Indicates whether caching is enabled.
     * 
     * @return boolean
     */
    public boolean isCachingEnabled() {
        return fCachingEnabled;
    }

    public void setCachingEnabled(boolean cachingEnabled) {
        if (fCachingEnabled != cachingEnabled) {
            fCachingEnabled = cachingEnabled;
            if (cachingEnabled & fCache == null)
                initializeCache(fInitialCapacity);
        }
    }

    /**
     * @return int initial capacity of the cache when cache is initialized
     */
    public int getInitialCapacity() {
        return fInitialCapacity;
    }

    /**
     * Sets the initial capacity of the cache. If the cache has already been
     * initialized, this method has no effect until the cache is released ({@link #releaseCache()})
     * and re-initialized ({@link #initializeCache(int)}).
     */
    public void setInitialCapacity(int capacity) {
        fInitialCapacity = capacity;
    }

    /**
     * Creates (or resets) the cache. Subclasses don't need to call this
     * directly; it is called via the {@link #setCachingEnabled(boolean)}
     * method.
     */
    protected void initializeCache(int capacity) {
        fCache = new HashMap<String, String>(capacity);
    }

    /**
     * Destroys the cache. Intended for internal use by subclasses.
     */
    protected void releaseCache() {
        fCache = null;
    }

    /**
     * Returns null if caching is not enabled.
     */
    protected String getCachedLine(String key) {
        if (!isCachingEnabled()) return null;
        if (key == null | fCache == null) return null;
        return fCache.get(key);
    }

    /**
     * Does nothing if caching is not enabled.
     */
    protected void cacheLine(String key, String line) {
        if (isCachingEnabled() & line != null & key != null & fCache != null)
            fCache.put(key, line);
    }

}
