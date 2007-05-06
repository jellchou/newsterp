/******************************************************************************
 * Copyright (c) 2007 Mark Alan Finlayson
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT Java Wordnet Interface 
 * Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.mit.edu/~markaf/projects/wordnet/license.html.
 *****************************************************************************/

package edu.mit.jwi.dict;

public interface IClassCache {

	/**
	 * Standard default initial capacity
	 */
	public static final int DEFAULT_INITIAL_CAPACITY = 500;

	/**
	 * Standard default maximum capacity
	 */
	public static final int DEFAULT_MAXIMUM_CAPACITY = DEFAULT_INITIAL_CAPACITY * 10;

	public boolean isCachingEnabled();

	/**
	 * Turns caching on of off.
	 * 
	 * @param cachingEnabled
	 */
	public void setCachingEnabled(boolean cachingEnabled);

	/**
	 * @return the initial capacity for all caches
	 */
	public int getInitialCapacity();

	public void setInitialCapacity(int capacity);

	/**
	 * @return the maximum capacity for all caches
	 */
	public int getMaximumCapacity();

	/**
	 * Although the maximum capacity <b>can</b> be set to less than the initial
	 * capacity, this is really a waste. If any caches are larger than the new
	 * maxium capacity, then are immediately pared down to size.
	 * 
	 * @param capacity
	 */
	public void setMaximumCapacity(int capacity);

	/**
	 * @return the size of the cache for the specified type. If no cache for
	 *         that specific class exists, returns -1;
	 */
	public int getCacheSize(Class type);

	/**
	 * Removes all items from all caches.
	 */
	public void clearCaches();

	/**
	 * Removes all items from the cache for the specified type.
	 */
	public void clearCache(Class type);

	/**
	 * Allocates resources for cache for the specified class.
	 */
	public void initializeCache(Class c);

	/**
	 * Allocates resources for caches for the specified classes.
	 */
	public void initializeCaches(Class[] cs);

	/**
	 * @return <code>true</code> if a cache for the specified class is
	 *         initialized; <code>false</code> otherwise.
	 */
	public boolean isCacheInitialized(Class c);

	/**
	 * Frees the resources associated with the cache for the specifed class.
	 * 
	 * @param c
	 */
	public void releaseCache(Class c);

	/**
	 * Frees the resources associated with caches for the specified classes.
	 */
	public void releaseCaches(Class[] cs);

	/**
	 * Tries to cache this item under an appropriate class cache.
	 * 
	 * @return the class under which the item was cached, or <code>null</code>
	 *         if the item was not succesfully cached (because, for example,
	 *         there is no cache of a compatible type)
	 */
	public Class cache(Object key, Object item);

	/**
	 * Where the actual caching occurs. Maintains the size of the cache less
	 * than maximum.
	 * 
	 * @return <code>true</code> if the item was cached or was already in the
	 *         cache; <code>false</code> if the cache doesn't exist or the
	 *         item is not compatible with that cache type
	 */
	public boolean cache(Object key, Object item, Class type);

	/**
	 * Tries to retrieve an item held under the specified key in the cache for
	 * the specified class.
	 * 
	 * @return the object stored, or <code>null</code> if there is not object
	 *         stored under that key in that cache, or the cache doesn't exist.
	 */
	public Object retrieve(Object key, Class type);

}
