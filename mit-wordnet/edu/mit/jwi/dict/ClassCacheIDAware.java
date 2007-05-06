/******************************************************************************
 * Copyright (c) 2007 Mark Alan Finlayson
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT Java Wordnet Interface 
 * Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.mit.edu/~markaf/projects/wordnet/license.html.
 *****************************************************************************/

package edu.mit.jwi.dict;

import edu.mit.jwi.item.IHasID;
import edu.mit.jwi.item.IItemID;

/**
 * Default implementation of the <tt>IClassCacheIDAware</tt> interface.
 * 
 * @author Mark A. Finlayson
 * @version 1.1, 4/28/07
 * @since 1.5
 */
public class ClassCacheIDAware extends ClassCache implements IClassCacheIDAware {



	public ClassCacheIDAware() {
		super();
	}
	
	public ClassCacheIDAware(int initialCapacity, int maxCapacity) {
		super(initialCapacity, maxCapacity);
	}
	
	public ClassCacheIDAware(Class c) {
		super(c);
	}

	public ClassCacheIDAware(Class[] cs) {
		super(cs);
	}

	public ClassCacheIDAware(int initialCapacity, int maxCapacity, Class c) {
		super(initialCapacity, maxCapacity, c);
	}

	public ClassCacheIDAware(int initialCapacity, int maxCapacity, Class[] cs) {
		super(initialCapacity, maxCapacity, cs);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.dict.IIDCache#cache(edu.mit.jwi.item.IHasID)
	 */
	public Class cache(IHasID item) {
		Class type = item.getID().getIdentifiedClass();
		return (cache(item.getID(), item, type) ? type : null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.dict.IIDCache#retrieve(edu.mit.jwi.item.IItemID)
	 */
	public Object retrieve(IItemID key) {
		return retrieve(key, key.getIdentifiedClass());
	}
}