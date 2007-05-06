/******************************************************************************
 * Copyright (c) 2007 Mark Alan Finlayson
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT Java Wordnet Interface 
 * Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.mit.edu/~markaf/projects/wordnet/license.html.
 *****************************************************************************/

package edu.mit.jwi.item;

/**
 * Represents the data that can be obtained from an exception entry file. Since
 * a full {@link edu.mit.jwi.item.IExceptionEntry} contains the
 * {@link edu.mit.jwi.item.PartOfSpeech} associated with the entry, this
 * object is just a 'proxy' and must be supplemented by the part of speech at
 * some point to make a full <tt>IExceptionEntry</tt> object.
 * 
 * @author Mark A. Finlayson
 * @version 1.00, 4/20/07
 * @since 1.5.0
 */
public interface IExceptionEntryProxy {

    /**
     * The surface form (i.e., not root form) of the exception entry.
     * 
     * @return String
     */
    public String getSurfaceForm();

    /**
     * Acceptable root forms for the surface form.
     * 
     * @return String[] An array of root forms
     */
    public String[] getRootForms();

}
