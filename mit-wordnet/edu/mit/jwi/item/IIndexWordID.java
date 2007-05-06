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
 * A unique identifier sufficient to retrieve a specific index word from the
 * Wordnet database. It consists of both a lemma (root form) and part of speech.
 * 
 * @author Mark A. Finlayson
 * @version 1.00, 4/14/06
 * @since 1.5.0
 */
public interface IIndexWordID extends IHasPartOfSpeech, IItemID<IIndexWord> {

    /**
     * The lemma (root form) of the index word.
     * 
     * @return String
     */
    public String getLemma();

}
