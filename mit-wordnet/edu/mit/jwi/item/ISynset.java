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
 * Represents a synset object.
 * 
 * @author Mark A. Finlayson
 * @version 1.00, 4/16/07
 * @since 1.5.0
 */
public interface ISynset extends IHasPartOfSpeech, IHasID<ISynsetID> {

    /**
     * Returns the data file offset of this synset, per the Wordnet
     * specification.
     * 
     * @return long the offset in the associated data source
     */
    public long getOffset();

    /**
     * The gloss (brief plain English description) of this synset.
     * 
     * @return String The gloss
     */
    public String getGloss();

    /**
     * Returns all the word objects (synset, index word pairs) associated with
     * this synset.
     * 
     * @return IWord[] An array of the words associated
     */
    public IWord[] getWords();

    /**
     * Gets all ids of all synsets that are related to this synset by the
     * specified pointer type.
     * 
     * @return ISynsetID[]
     */
    public ISynsetID[] getRelatedSynsets(IPointerType type);

    /**
     * Returns an array of synset ids for all synsets that are connected by
     * pointers to this synset
     * 
     * @return ISynsetID[]
     */
    public ISynsetID[] getRelatedSynsets();

    /**
     * Returns whether this synset is an adjective satellite or not, per Wordnet
     * specification.
     * 
     * @return boolean
     */
    public boolean isAdjectiveSatellite();

    /**
     * Returns whether this synset is an adjective head or not, per Wordnet
     * specification
     * 
     * @return boolean
     */
    public boolean isAdjectiveHead();

}
