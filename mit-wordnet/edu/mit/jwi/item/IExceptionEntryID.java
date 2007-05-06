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
 * A unique identifier sufficient to retrieve the specified
 * {@link edu.mit.jwi.item.IExceptionEntry} from the Wordnet database.
 * 
 * @author Mark A. Finlayson
 * @version 1.00, 4/13/07
 * @since 1.5.0
 */
public interface IExceptionEntryID extends IHasPartOfSpeech, IItemID<IExceptionEntry> {

    /**
     * The surface form (i.e., not the root form) of the word for which a
     * morphological exception entry is desired.
     * 
     * @return String
     */
    public String getSurfaceForm();

}
