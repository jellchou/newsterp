/******************************************************************************
 * Copyright (c) 2007 Mark Alan Finlayson
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT Java Wordnet Interface 
 * Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.mit.edu/~markaf/projects/wordnet/license.html.
 *****************************************************************************/

package edu.mit.jwi.data;

/**
 * Objects that implement this interface represent possible data classes that
 * occur in the dictionary data directory. The easiest way to do this in Java
 * 1.5.0 is to use an <code>enum</code> implementation.
 * <p>
 * In the standard Wordnet 2.1 distribution, file types would include, but would
 * not be limited to, <i>Index</i> files, <i>Data</i> files, and <i>Exception</i>
 * files. The objects implementing this interface are then paired with an
 * {@link edu.mit.jwi.item.PartOfSpeech} object to form an instance of an
 * {@link edu.mit.jwi.content.IContentType} class, which identifies the
 * specific data contained in the file. Note that here, 'file' refers not to an
 * actual file, but to an instance of the <tt>IDictionaryFile</tt> interface
 * that provides access to the data, be it a file in the file system, a socket
 * connection to a database, or something else.
 * 
 * @author Mark A. Finlayson
 * @version 1.00, 04/15/06
 * @since 1.5.0
 */
public interface IDictionaryDataType {

    /**
     * Returns an array of strings that can be used as keywords to identify
     * resources that are of this type.
     * @return String[] array of resource name fragments
     */
    public String[] getResourceNameHints();
}
