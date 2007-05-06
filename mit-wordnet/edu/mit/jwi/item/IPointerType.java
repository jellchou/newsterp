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
 * Objects that implement this interface represent all possible types of
 * pointers that are contained in the dictionary data resources. The easiest way
 * to do this in Java 1.5.0 is to use an <code>enum</code> implementation.
 * 
 * @author Mark A. Finlayson
 * @version 1.00, 4/12/06
 * @since 1.5.0
 */
public interface IPointerType {

    /**
     * The symbol in the Wordnet data files that is used to indicate this
     * pointer type.
     */
    public String getSymbol();

    /**
     * Returns a user-friendly name of this pointer type for identification
     * purposes.
     */
    public String getName();

}
