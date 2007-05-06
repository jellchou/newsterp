/******************************************************************************
 * Copyright (c) 2007 Mark Alan Finlayson
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT Java Wordnet Interface 
 * Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.mit.edu/~markaf/projects/wordnet/license.html.
 *****************************************************************************/

package edu.mit.jwi.content.parse;

import java.util.HashMap;
import java.util.Map;

import edu.mit.jwi.content.IContentType;
import edu.mit.jwi.content.IParserProvider;
import edu.mit.jwi.data.IDictionaryDataType;
import edu.mit.jwi.data.WordnetDataType;

/**
 * Basic implementation of a parser provider. If it cannot find a parser
 * registered under the specified contentType, it checks the corresponding
 * <tt>IDictionaryFileType</tt> before returning <code>null</code>
 * 
 * @author Mark A. Finlayson
 * @version 1.00, 04/13/06
 * @since 1.5.0
 */
public class ParserProvider implements IParserProvider {

    private Map<IDictionaryDataType, ILineParser> dataTypeMap;
    private Map<IContentType, ILineParser> contentTypeMap;

    public ParserProvider() {
        registerParser(WordnetDataType.DATA, new DataLineParser());
        registerParser(WordnetDataType.INDEX, new IndexLineParser());
        registerParser(WordnetDataType.EXCEPTION, new ExceptionLineParser());
    }

    /**
     * If it cannot find a parser registered under the specified contentType, it
     * checks the corresponding <tt>IDictionaryFileType</tt> before returning
     * <code>null</code>.
     * 
     * @see edu.mit.jwi.content.IParserProvider#getParser(edu.mit.jwi.content.IContentType)
     */
    public ILineParser getParser(IContentType contentType) {
        ILineParser result = contentTypeMap.get(contentType);
        if (result == null) return getParser(contentType.getDataType());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.jwi.core.content.IParserProvider#registerParser(edu.mit.jwi.core.content.IContentType,
     *      edu.mit.jwi.core.content.ILineParser)
     */
    public ILineParser registerParser(IContentType contentType,
            ILineParser parser) {
        if (contentTypeMap == null)
            contentTypeMap = new HashMap<IContentType, ILineParser>();
        return contentTypeMap.put(contentType, parser);
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.jwi.core.content.IParserProvider#getParser(edu.mit.jwi.core.file.IDictionaryFileType)
     */
    public ILineParser getParser(IDictionaryDataType fileType) {
        ILineParser result = dataTypeMap.get(fileType);
        if (result == null)
            throw new RuntimeException("No parser exists for file type "
                    + fileType);
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.jwi.core.content.IParserProvider#registerParser(edu.mit.jwi.core.file.IDictionaryFileType,
     *      edu.mit.jwi.core.content.ILineParser)
     */
    public ILineParser registerParser(IDictionaryDataType fileType,
            ILineParser parser) {
        if (dataTypeMap == null)
            dataTypeMap = new HashMap<IDictionaryDataType, ILineParser>();
        return dataTypeMap.put(fileType, parser);
    }

}
