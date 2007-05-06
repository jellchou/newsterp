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
 * Represents a unique identifier sufficient to retrieve a particular
 * {@link edu.mit.jwi.item.IWord} object from the Wordnet database.
 * 
 * @author Mark A. Finlayson
 * @version 1.00, 4/16/07
 * @since 1.5.0
 */
public interface IWordID extends IHasPartOfSpeech, IItemID<IWord> {

    /**
     * Returns the SynsetID object associated with this IWordID.
     * 
     * @return ISynsetID
     */
    public ISynsetID getSynsetID();

    /**
     * The word number, as described in the Wordnet specification.
     * 
     * @return int
     */
    public int getNumber();

    /**
     * Returns the lemma (word root) associated with this index word.
     * 
     * @return String the lemma
     */
    public String getLemma();
}
