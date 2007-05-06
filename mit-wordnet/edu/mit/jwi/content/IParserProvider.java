/******************************************************************************
 * Copyright (c) 2007 Mark Alan Finlayson
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT Java Wordnet Interface 
 * Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.mit.edu/~markaf/projects/wordnet/license.html.
 *****************************************************************************/

package edu.mit.jwi.content;

import edu.mit.jwi.content.parse.ILineParser;
import edu.mit.jwi.data.IDictionaryDataType;

/**
 * Objects that implement this interface provide objects of type
 * <tt>ILineParser</tt> for specified content types. <tt>IDictionary</tt>
 * objects can use instances of this interface to automatically determine which
 * parsers to use for what data resources.
 * 
 * @author Mark A. Finlayson
 * @version 1.00, 04/15/06
 * @since 1.5.0
 */
public interface IParserProvider {

    /**
     * Gets a parser that is registered for the particular content type. If no
     * parser is found, returns <code>null</code>.
     */
    public ILineParser getParser(IContentType contentType);

    /**
     * Gets a parser that is registered for the particular data type. If no
     * parser is found, returns <code>null</code>.
     */
    public ILineParser getParser(IDictionaryDataType type);

    /**
     * Registers a parser under the specified content type. If this registration
     * replaces a previous parser, that one is returned.
     */
    public ILineParser registerParser(IContentType contentType,
            ILineParser parser);

    /**
     * Registers a parser under the specified data type. If this registration
     * replaces a previous parser, that one is returned.
     */
    public ILineParser registerParser(IDictionaryDataType type,
            ILineParser parser);

}
