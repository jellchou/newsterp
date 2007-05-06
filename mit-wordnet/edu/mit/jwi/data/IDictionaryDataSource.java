/******************************************************************************
 * Copyright (c) 2007 Mark Alan Finlayson
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT Java Wordnet Interface 
 * Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.mit.edu/~markaf/projects/wordnet/license.html.
 *****************************************************************************/

package edu.mit.jwi.data;

import java.util.Iterator;

import edu.mit.jwi.content.IContentType;
import edu.mit.jwi.content.compare.ICommentDetector;

/**
 * Objects that implement this interface mediate between the
 * IDictionary and the data the is contained in the dictionary data resources.
 * They typically are assigned a name (e.g., <i>verb.data</i>, for the data
 * resource pertaining to verbs) and a content type. A string key is used to
 * find a particular piece of data in the resource. The <tt>IDictionaryFile</tt>
 * object then returns a text string that can be parsed to produce a data object
 * (e.g., an <tt>ISynset</tt> or <tt>IIndexWord</tt> object). To speed up
 * searches in the data, it is recommended the data contained in the resource
 * backing the <tt>IDictionaryFile</tt> be ordered, and a comparator that
 * takes advantage of this order be provided to the internal search mechanism of
 * the class.
 * 
 * @author Mark A. Finlayson
 * @version 1.00, 04/15/06
 * @since 1.5.0
 */
public interface IDictionaryDataSource {

    /** Returns a string representation of the name of this resource */
    public String getName();

    /** Returns the assigned content type of the resource that backs this object */
    public IContentType getContentType();

    /**
     * Sets the class that is used to detect whether a specific line of data is
     * a comment. Calling this method might not be necessary in some
     * implementations.
     */
    public void setCommentDetector(ICommentDetector detector);

    /**
     * Returns the comment detector in use by this object.
     */
    public ICommentDetector getCommentDetector();

    /**
     * Returns a string that contains the data indexed by the specified key. If
     * the file cannot find the key in its data resource, it returns
     * <code>null</code>
     */
    public String getLine(String key);

    /**
     * Returns an iterator that will iterator over all lines in the data
     * resource.
     */
    public Iterator<String> iterator();

    /**
     * Returns an iterator that will iterator over lines in the data resource,
     * starting at the line specified by the given key. If said line is not
     * found, or the key is null, the iterator will start at the beginning of
     * the file.
     */
    public Iterator<String> iterator(String key);

}
