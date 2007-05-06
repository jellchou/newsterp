/******************************************************************************
 * Copyright (c) 2007 Mark Alan Finlayson
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT Java Wordnet Interface 
 * Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.mit.edu/~markaf/projects/wordnet/license.html.
 *****************************************************************************/

package edu.mit.jwi.content.parse;

/**
 * Objects that implement this interface are used to parse lines of data from
 * data resource into data structures that are then manipulated by the
 * dictionary.
 * 
 * @author Mark A. Finlayson
 * @version 1.00, 04/15/06
 * @since 1.5.0
 */
public interface ILineParser<T> {

    /**
     * Given the line of data, it produces an object of class <tt>T</tt>. If
     * the line is <code>null</code>, or the line is malformed in some way,
     * the method throws a <tt>MisformattedLineException</tt>.
     * 
     * @param line
     *            the line that should be parsed
     * @return the object resulting from the parse
     * @throws edu.mit.wordnet.content.MisformattedLineException
     *             if the line is malformed or null
     */
    public T parseLine(String line);

}
