/******************************************************************************
 * Copyright (c) 2007 Mark Alan Finlayson
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT Java Wordnet Interface 
 * Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.mit.edu/~markaf/projects/wordnet/license.html.
 *****************************************************************************/

package edu.mit.jwi.content.parse;

import edu.mit.jwi.content.MisformattedLineException;
import edu.mit.jwi.item.ExceptionEntryProxy;
import edu.mit.jwi.item.IExceptionEntryProxy;

/**
 * Basic implementation of an <tt>ILineParser</tt> that takes a line from a
 * Wordnet 2.1/3.0 exception file (exc.adv or adv.exc files, for example) and
 * produces an <tt>IExceptionEntryProxy</tt> object. This parser produces
 * <tt>IExceptionEntryProxy</tt> objects instead of <tt>IExceptionEntry</tt>
 * objects directly because the exception files do not contain information about
 * part of speech. This needs to be added by the <tt>IDictionary</tt> object
 * using this line parser to create a full-fledged <tt>IExceptionEntry</tt>
 * object.
 * 
 * @author Mark A. Finlayson
 * @version 1.1, 04/28/07
 * @since 1.5.0
 */
public class ExceptionLineParser implements ILineParser<IExceptionEntryProxy> {

    /* (non-Javadoc) @see edu.mit.wordnet.core.file.ILineParser#parseIndexLine(java.lang.String) */
    public IExceptionEntryProxy parseLine(String line) {
        if (line == null)
            throw new MisformattedLineException(line);
        IExceptionEntryProxy result = null;

        String[] forms = line.split(" ");
        if (forms.length < 2)
            throw new MisformattedLineException(line);

        String[] trimmed = new String[forms.length];
        for (int i = 0; i < forms.length; i++)
            trimmed[i] = forms[i].trim();

        result = new ExceptionEntryProxy(trimmed);
        return result;
    }
}
