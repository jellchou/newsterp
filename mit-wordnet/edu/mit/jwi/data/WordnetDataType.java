/******************************************************************************
 * Copyright (c) 2007 Mark Alan Finlayson
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT Java Wordnet Interface 
 * Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.mit.edu/~markaf/projects/wordnet/license.html.
 *****************************************************************************/

package edu.mit.jwi.data;

/**
 * Basic implmentation of <tt>IDictionaryFileType</tt> interface. Enumerates
 * all file types contained in the Wordnet 2.1 distribution.
 * 
 * @author Mark A. Finlayson
 * @version 1.00, 04/15/06
 * @since 1.5.0
 */
public enum WordnetDataType implements IDictionaryDataType {

    INDEX("Index", new String[] { "index", "idx" }), DATA("Data", new String[] {
            "data", "dat" }), COUNT("Count", new String[] { "count", "cnt" }), EXCEPTION(
            "Exception", new String[] { "exception", "exc" });

    private final String toString;
    private final String[] hints;

    /**
     * Because this is an enum type, the constructor should not be called
     * outside of the class.
     */
    private WordnetDataType(String userFriendlyName, String[] hints) {
        this.toString = userFriendlyName;
        this.hints = hints;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.file.IDictionaryFileType#getPatterns()
     */
    public String[] getResourceNameHints() {
        return hints;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return toString;
    }

}
