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
 * Represents a unique identifier for a {@link edu.mit.jwi.item.ISynset},
 * sufficient to retrieve the synset from the Wordnet database. It consists of a
 * part of speech and an offset.
 * 
 * @author Mark A. Finlayson
 * @version 1.00, 4/12/06
 * @since 1.5.0
 */
public interface ISynsetID extends IHasPartOfSpeech, IItemID<ISynset> {

    /**
     * Returns the offset for the specified synset.
     */
    public long getOffset();

}
