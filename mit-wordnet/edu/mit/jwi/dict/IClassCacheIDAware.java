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
 * Extension of the <tt>IClassCache</tt> interface that knows how to handle
 * items that implement the <tt>IHasID</tt> iterface.
 * 
 * @author Mark A. Finlayson
 * @version 1.00, 4/28/07
 * @since 1.5.0
 */
public interface IClassCacheIDAware extends IClassCache {

	/**
	 * Tries to cache this item under an appropriate class.
	 * 
	 * @return the class under which the item was cached, or <code>null</code>
	 *         if the item was not succesfully cached (because, for example,
	 *         there is no cache of a compatible type)
	 */
	public Class cache(IHasID item);

	/**
	 * @param id
	 *            The id of the item which is desired
	 * @return The item cached for the specified id, if found. <code>null</code>
	 *         otherwise.
	 */
	public Object retrieve(IItemID id);

}
