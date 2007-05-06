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
 * Represents an entry in an exception file (e.g., verb.exc or exc.vrb). Most of
 * the functionality of this class comes from
 * {@link edu.mit.jwi.item.IExceptionEntryProxy}
 * 
 * @author Mark A. Finlayson
 * @version 1.00, 4/13/07
 * @since 1.5.0
 */
public interface IExceptionEntry extends IExceptionEntryProxy, IHasPartOfSpeech, IHasID<IExceptionEntryID> {

}
