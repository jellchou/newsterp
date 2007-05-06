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
 * Represents a word (a pair of an {@link edu.mit.jwi.item.IIndexWord} and
 * {@link edu.mit.jwi.item.ISynset}) in the Wordnet database.
 * 
 * @author Mark A. Finlayson
 * @version 1.00, 4/14/06
 * @since 1.5.0
 */
public interface IWord extends IHasPartOfSpeech, IHasID<IWordID> {

    /**
     * Returns the root form of this word.
     * 
     * @return String
     */
    public String getLemma();

    /**
     * Returns the synset gloss for this word.
     * 
     * @return String
     */
    public String getGloss();

    /**
     * Returns the id of the synset uniquely identified with this word.
     * 
     * @return ISynsetID
     */
    public ISynsetID getSynsetID();

    /**
     * Gets all words related to this word by the specified pointer type.
     * 
     * @return IWordID[]
     */
    public IWordID[] getRelatedWords(IPointerType type);

    /**
     * Gets all word objects related to this word by pointers in the database.
     * 
     * @return IWordID[]
     */
    public IWordID[] getAllRelatedWords();

    /**
     * Gets all verb frames associated with this word.
     * 
     * @return IVerbFrame[]
     */
    public IVerbFrame[] getVerbFrames();

    /**
     * Returns the adjective marker of this word, as specified in the Wordnet
     * database.
     * 
     * @return String
     */
    public String getAdjectiveMarker();

}
