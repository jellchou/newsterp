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
 * This interface indicates that object that implements it may have an
 * associated part of speech.
 * 
 * @author Mark Alan Finlayson
 * @version 1.00, 4/16/06
 * @since 1.5.0
 */
public interface IHasPartOfSpeech {

    /**
     * Returns which part of speech this object pertains to. May be
     * <code>null</code>, if the object is not specific to any particular
     * part of speech.
     */
    public PartOfSpeech getPartOfSpeech();
}
