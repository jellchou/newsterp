/******************************************************************************
 * Copyright (c) 2007 Mark Alan Finlayson
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT Java Wordnet Interface 
 * Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.mit.edu/~markaf/projects/wordnet/license.html.
 *****************************************************************************/

package edu.mit.jwi.dict;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Default implementation of the IClassCache interface.
 * 
 * @author Mark A. Finlayson
 * @version 1.1, 4/28/07
 * @since 1.5
 */
public class ClassCache implements IClassCache {

	/**
	 * Flag that records whether caching is enabled for this dictionary. Default
	 * starting state is <code>true</code>.
	 */
	boolean fCachingEnabled = true;

	/**
	 * Initial capacity of the caches.
	 */
	int fInitialCapacity;

	/**
	 * Maximum capacity of the caches. If this is set to less than zero, then
	 * the cache size is unlimited.
	 */
	int fMaximumCapacity;

	private Map<Class, Map> caches = new HashMap<Class, Map>();

	/**
	 * Default constructor that initializes the dictionary with caching enabled.
	 */
	public ClassCache() {
		this(DEFAULT_INITIAL_CAPACITY, DEFAULT_MAXIMUM_CAPACITY);
	}

	/**
	 * User can specify both the initial size of the cache and the initial state
	 * of caching.
	 */
	public ClassCache(int initialCapacity, int maxCapacity) {
		this(initialCapacity, maxCapacity, (Class[])null);
	}
	
	/**
	 * User can specify both the initial size of the cache and the initial state
	 * of caching; the cache for the specified class is also initialized
	 */
	public ClassCache(Class c) {
		this(DEFAULT_INITIAL_CAPACITY, DEFAULT_MAXIMUM_CAPACITY, new Class[]{c});
	}
	
	/**
	 * User can specify both the initial size of the cache and the initial state
	 * of caching; the cache for the specified class is also initialized
	 */
	public ClassCache(Class[] cs) {
		this(DEFAULT_INITIAL_CAPACITY, DEFAULT_MAXIMUM_CAPACITY, cs);
	}
	
	/**
	 * User can specify both the initial size of the cache and the initial state
	 * of caching; the cache for the specified class is also initialized
	 */
	public ClassCache(int initialCapacity, int maxCapacity, Class c) {
		this(initialCapacity, maxCapacity, new Class[]{c});
	}
	
	/**
	 * User can specify both the initial size of the cache and the initial state
	 * of caching; the cache for the specified class is also initialized
	 */
	public ClassCache(int initialCapacity, int maxCapacity, Class[] cs) {
		setInitialCapacity(initialCapacity);
		setMaximumCapacity(maxCapacity);
		initializeCaches(cs);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.dict.IClassCache#isCachingEnabled()
	 */
	public boolean isCachingEnabled() {
		return fCachingEnabled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.dict.IClassCache#setCachingEnabled(boolean)
	 */
	public void setCachingEnabled(boolean cachingEnabled) {
		fCachingEnabled = cachingEnabled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.dict.IClassCache#getInitialCapacity()
	 */
	public int getInitialCapacity() {
		return fInitialCapacity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.dict.IClassCache#setInitialCapacity(int)
	 */
	public void setInitialCapacity(int capacity) {
		fInitialCapacity = capacity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.dict.IClassCache#getMaximumCapacity()
	 */
	public int getMaximumCapacity() {
		return fMaximumCapacity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.dict.IClassCache#setMaximumCapacity(int)
	 */
	public void setMaximumCapacity(int capacity) {
		fMaximumCapacity = capacity;
		if (fMaximumCapacity > -1) {
			for (Map cache : caches.values()) {
				if (cache.size() > fMaximumCapacity) {
					int remove = cache.size() - fMaximumCapacity;
					Iterator itr = cache.keySet().iterator();
					for (int i = 0; i <= remove; i++) {
						if (!itr.hasNext()) break;
						itr.next();
						itr.remove();
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.dict.IClassCache#getCacheSize(java.lang.Class)
	 */
	public int getCacheSize(Class type) {
		Map cache = caches.get(type);
		return (cache != null ? cache.size() : -1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.dict.IClassCache#clearCaches()
	 */
	public void clearCaches() {
		for (Class c : caches.keySet())
			caches.remove(c);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.dict.IClassCache#clearCache(java.lang.Class)
	 */
	public void clearCache(Class type) {
		Map cache = caches.get(type);
		if (cache != null) cache.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.dict.IClassCache#initializeCache(java.lang.Class)
	 */
	public void initializeCache(Class c) {
		if(c == null) return;
		caches.put(c, new LinkedHashMap(fInitialCapacity));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.dict.IClassCache#initializeCaches(java.lang.Class[])
	 */
	public void initializeCaches(Class[] cs) {
		for (Class c : cs)
			initializeCache(c);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.dict.IClassCache#isCacheInitialized(java.lang.Class)
	 */
	public boolean isCacheInitialized(Class c) {
		return caches.get(c) != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.dict.IClassCache#releaseCache(java.lang.Class)
	 */
	public void releaseCache(Class c) {
		caches.remove(c);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.dict.IClassCache#releaseCaches(java.lang.Class[])
	 */
	public void releaseCaches(Class[] cs) {
		for (Class c : cs)
			releaseCache(c);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.dict.IClassCache#cache(java.lang.Object,
	 *      java.lang.Object)
	 */
	public Class cache(Object key, Object item) {
		if (!isCachingEnabled() | key == null | item == null | caches == null) return null;

		if (cache(key, item, item.getClass())) return item.getClass();

		for (Map.Entry<Class, Map> entry : caches.entrySet()) {
			if (entry.getKey().isAssignableFrom(item.getClass())) {
				if (cache(key, item, entry.getKey())) return entry.getKey();
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.dict.IClassCache#cache(java.lang.Object,
	 *      java.lang.Object, java.lang.Class)
	 */
	public boolean cache(Object key, Object item, Class type) {
		if (!isCachingEnabled() | key == null | item == null | type == null) return false;
		Map cache = caches.get(type);
		if (cache == null) return false;
		if (!type.isAssignableFrom(item.getClass())) return false;
		cache.put(key, item);

		if (fMaximumCapacity < 0) return true;

		// make sure cache is less than maximum size
		if (cache.size() > fMaximumCapacity) {
			Iterator itr = cache.keySet().iterator();
			itr.next();
			itr.remove();
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.dict.IClassCache#retrieve(java.lang.Object,
	 *      java.lang.Class)
	 */
	public Object retrieve(Object key, Class type) {
		if (!isCachingEnabled()) return null;
		if (key == null | type == null | caches == null) return null;
		Map cache = caches.get(type);
		if (cache == null) return null;
		return cache.get(key);
	}

}
