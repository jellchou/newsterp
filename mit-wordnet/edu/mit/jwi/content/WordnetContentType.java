/******************************************************************************
 * Copyright (c) 2007 Mark Alan Finlayson
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT Java Wordnet Interface 
 * Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.mit.edu/~markaf/projects/wordnet/license.html.
 *****************************************************************************/

package edu.mit.jwi.content;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import edu.mit.jwi.content.compare.StaticComparators;
import edu.mit.jwi.data.IDictionaryDataType;
import edu.mit.jwi.data.WordnetDataType;
import edu.mit.jwi.item.PartOfSpeech;

/**
 * A basic implementation of the IContentType interface, tuned to the Wordnet
 * 2.1 distribution.
 * 
 * @author Mark A. Finlayson
 * @version 1.00, 04/15/06
 * @since 1.5.0
 */
public enum WordnetContentType implements IContentType {
    
	INDEX_NOUN (WordnetDataType.INDEX, PartOfSpeech.NOUN, StaticComparators.getIndexLineComparator()),
	INDEX_VERB (WordnetDataType.INDEX, PartOfSpeech.VERB, StaticComparators.getIndexLineComparator()),
	INDEX_ADVERB (WordnetDataType.INDEX, PartOfSpeech.ADVERB, StaticComparators.getIndexLineComparator()),
	INDEX_ADJECTIVE (WordnetDataType.INDEX, PartOfSpeech.ADJECTIVE, StaticComparators.getIndexLineComparator()),
	DATA_NOUN (WordnetDataType.DATA, PartOfSpeech.NOUN, StaticComparators.getDataLineComparator()),
	DATA_VERB (WordnetDataType.DATA, PartOfSpeech.VERB, StaticComparators.getDataLineComparator()),
	DATA_ADVERB (WordnetDataType.DATA, PartOfSpeech.ADVERB, StaticComparators.getDataLineComparator()),
	DATA_ADJECTIVE (WordnetDataType.DATA, PartOfSpeech.ADJECTIVE, StaticComparators.getDataLineComparator()),
	EXCEPTION_NOUN (WordnetDataType.EXCEPTION, PartOfSpeech.NOUN, StaticComparators.getExceptionComparator()),
	EXCEPTION_VERB (WordnetDataType.EXCEPTION, PartOfSpeech.VERB, StaticComparators.getExceptionComparator()),
	EXCEPTION_ADVERB (WordnetDataType.EXCEPTION, PartOfSpeech.ADVERB, StaticComparators.getExceptionComparator()),
	EXCEPTION_ADJECTIVE (WordnetDataType.EXCEPTION, PartOfSpeech.ADJECTIVE, StaticComparators.getExceptionComparator());

	private final IDictionaryDataType fType;
    private final PartOfSpeech fPOS;
    private final Comparator<String> fComparator;
    private final String fString;

    /**
     * Private constructor because this is an enum type.
     */
    private WordnetContentType(IDictionaryDataType type, PartOfSpeech pos,
            Comparator<String> comparator) {
        fType = type;
        fPOS = pos;
        fComparator = comparator;

        if (pos != null) {
            fString = "[ContentType: " + fType.toString() + "/"
                    + fPOS.toString() + "]";
        } else {
            fString = "[ContentType: " + fType.toString() + "]";
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.content.IWordnetContentType#getType()
     */
    public IDictionaryDataType getDataType() {
        return fType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.content.IWordnetContentType#getPartOfSpeech()
     */
    public PartOfSpeech getPartOfSpeech() {
        return fPOS;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.content.IWordnetContentType#getLineComparator()
     */
    public Comparator<String> getLineComparator() {
        return fComparator;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return fString;
    }

    /**
     * Static map to speed up accesses of content types
     */
    private static Map<PartOfSpeech, Map<IDictionaryDataType, IContentType>> posMap;

    /**
     * Static map to speed up accesses of content types
     */
    private static Map<IDictionaryDataType, IContentType> typeMap;

    static {
        posMap = new HashMap<PartOfSpeech, Map<IDictionaryDataType, IContentType>>();
        typeMap = new HashMap<IDictionaryDataType, IContentType>();

        WordnetContentType[] values = WordnetContentType.values();
        Map<IDictionaryDataType, IContentType> posTypeMap;
        PartOfSpeech pos;
        for (WordnetContentType content : values) {
            pos = content.getPartOfSpeech();
            if (pos != null) {
                posTypeMap = posMap.get(pos);

                // if map is null, instantiate it
                if (posTypeMap == null) {
                    posTypeMap = new HashMap<IDictionaryDataType, IContentType>();
                    posMap.put(content.getPartOfSpeech(), posTypeMap);
                }

                posTypeMap.put(content.getDataType(), content);
            } else {
                typeMap.put(content.getDataType(), content);
            }
        }
    }

    /**
     * Returns the content type (a static final instance) that matches the
     * specified part of speech and file type. If no content type is found for
     * the specified arguments, throws a <tt>RuntimeException</tt>. Also
     * throws a <tt>NullPointerException</tt> if the
     * <tt>IDictionaryDataType</tt> parameter is <code>null</code>
     * 
     * @param pos The part of speech of the content type requested; may be null
     * @param type The dictionary file type
     * @return the content type, if found; null otherwise
     */
    public static IContentType getContentType(PartOfSpeech pos,
            IDictionaryDataType type) {
        if (type == null)
            throw new NullPointerException("IDictionaryDataType cannot be null");

        IContentType contentType = null;
        if (pos != null) {
            Map<IDictionaryDataType, IContentType> posTypeMap = posMap.get(pos);
            if (posTypeMap == null)
                throw new RuntimeException("No content type exists for POS "
                        + pos);
            if (posTypeMap != null) contentType = posTypeMap.get(type);
            if (contentType == null)
                throw new RuntimeException(
                        "No content type exists for file type " + type
                                + " and POS " + pos);
        } else {
            contentType = typeMap.get(type);
            if (contentType == null)
                throw new RuntimeException(
                        "No content type exists for file type " + type);
        }

        return contentType;
    }

}
