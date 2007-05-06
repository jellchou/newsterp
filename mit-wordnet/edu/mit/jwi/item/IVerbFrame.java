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
 * Represents a verb frame drawn from the verb frames data file in the Wordnet
 * distribution
 * 
 * @author Mark A. Finlayson
 * @version 1.00, 4/14/06
 * @since 1.5.0
 */
public interface IVerbFrame {

    /**
     * The id number of this verb frame.
     */
    public int getNumber();

    /**
     * The string form of the template, drawn directly from the data file.
     */
    public String getTemplate();

    /**
     * Takes the supplied surface form of a verb and instantiates it into the
     * template for the verb frame. This is a convenience method for the simple
     * string replace operation required to effect this.  The method does no
     * morphological processing, nor even does it check to see if the passed in
     * word is actually a verb.
     */
    public String instantiateTemplate(String verb);

}
