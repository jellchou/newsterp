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
 * Indicates that the provider was not open when data was requested of it.
 * 
 * @author Mark A. Finlayson
 * @version 1.00, 4/28/07
 * @since 1.5.0
 */
public class DataProviderNotOpenException extends RuntimeException {

    private static final long serialVersionUID = 6422889552839992911L;

    public DataProviderNotOpenException() {
        super();
    }

    public DataProviderNotOpenException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataProviderNotOpenException(String message) {
        super(message);
    }

    public DataProviderNotOpenException(Throwable cause) {
        super(cause);
    }

}
