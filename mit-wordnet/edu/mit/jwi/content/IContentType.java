/******************************************************************************
 * Copyright (c) 2007 Mark Alan Finlayson
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT Java Wordnet Interface 
 * Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.mit.edu/~markaf/projects/wordnet/license.html.
 *****************************************************************************/

package edu.mit.jwi.content;

import java.util.Comparator;

import edu.mit.jwi.data.IDictionaryDataType;
import edu.mit.jwi.item.IHasPartOfSpeech;

/**
 * Objects that implement this interface represent all possible types of content
 * that are contained in the dictionary data resources. Each unique object of
 * this type will usually correspond to a particular resource or file. The
 * easiest way to do this in Java 1.5.0 is to use an <code>enum</code>
 * implementation.
 * <p>
 * In the standard Wordnet 2.1 distribution, examples of content types would
 * include, but would not be limited to, <i>Noun Index</i>, <i>Noun Data</i>,
 * and <i>Noun Exception</i> files.
 * 
 * @author Mark A. Finlayson
 * @version 1.00, 04/15/06
 * @since 1.5.0
 */
public interface IContentType extends IHasPartOfSpeech {

    /**
     * Returns the assigned resource type of this object.
     */
    public IDictionaryDataType getDataType();

    /**
     * Returns a comparator that can be used to determine ordering between
     * different lines of data in the resource. This is used for efficient
     * searching. If the data in the resource is not ordered, then this method
     * returns <code>null</code>.
     */
    public Comparator<String> getLineComparator();

}
