/******************************************************************************
 * Copyright (c) 2007 Mark Alan Finlayson
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT Java Wordnet Interface 
 * Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.mit.edu/~markaf/projects/wordnet/license.html.
 *****************************************************************************/

package edu.mit.jwi.dict;

/**
 * Indicates that the dictionary was not open when data was requested of it.
 * 
 * @author Mark A. Finlayson
 * @version 1.00, 4/20/07
 * @since 1.5.0
 */
public class DictionaryNotOpenException extends RuntimeException {

    private static final long serialVersionUID = 6422889552839992911L;

    public DictionaryNotOpenException() {
        super();
    }

    public DictionaryNotOpenException(String message, Throwable cause) {
        super(message, cause);
    }

    public DictionaryNotOpenException(String message) {
        super(message);
    }

    public DictionaryNotOpenException(Throwable cause) {
        super(cause);
    }

}
