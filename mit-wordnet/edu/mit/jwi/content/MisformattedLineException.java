/******************************************************************************
 * Copyright (c) 2007 Mark Alan Finlayson
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT Java Wordnet Interface 
 * Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.mit.edu/~markaf/projects/wordnet/license.html.
 *****************************************************************************/

package edu.mit.jwi.content;

/**
 * Thrown when a line from a data resource does not match expected formatting
 * conventions.
 * 
 * @author Mark A. Finlayson
 * @version 1.00, 04/15/06
 * @since 1.5.0
 */
public class MisformattedLineException extends RuntimeException {

    private static final long serialVersionUID = -8689961245666935081L;

    private static String fHeader = "Misformatted line: ";

    public MisformattedLineException(String line) {
        super(fHeader + line);
    }

    public MisformattedLineException(String line, Throwable e) {
        super(fHeader + line, e);
    }

}
