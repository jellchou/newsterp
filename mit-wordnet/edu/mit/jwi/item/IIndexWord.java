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
 * Represents an index word object.
 * 
 * @author Mark A. Finlayson
 * @version 1.00, 4/20/07
 * @since 1.5.0
 */
public interface IIndexWord extends IHasPartOfSpeech, IHasID<IIndexWordID> {

    /**
     * Returns the lemma (word root) associated with this index word.
     * 
     * @return String the lemma
     */
    public String getLemma();

    /**
     * Returns ids of all words (index word+synset pairs) assocated with this
     * index word.
     * 
     * @return IWordID[]
     */
    public IWordID[] getWordIDs();

}
