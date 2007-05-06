/******************************************************************************
 * Copyright (c) 2007 Mark Alan Finlayson
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT Java Wordnet Interface 
 * Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.mit.edu/~markaf/projects/wordnet/license.html.
 *****************************************************************************/

package edu.mit.jwi.item;

import java.util.HashMap;
import java.util.Map;

/**
 * Default, hardcoded, implementation of <tt>IPointerType</tt> for Wordnet
 * 2.1 & 3.0.
 * 
 * @author Mark A. Finlayson
 * @version 1.00, 4/13/06
 * @since 1.5.0
 */
public enum WordnetPointerType implements IPointerType {
    
	ANTONYM ("!", "Antonym"),
	HYPERNYM ("@", "Hypernym"),
	HYPERNYM_INSTANCE ("@i", "Hypernym, Instance"),
	HYPONYM ("~", "Hyponym"),
	HYPONYM_INSTANCE ("~i", "Hyponym, Instance"),
	HOLONYM_MEMBER ("#m", "Holonym, Member"),
	HOLONYM_SUBSTANCE ("#s", "Holonym, Substance"),
	HOLONYM_PART ("#p", "Holonym, Part"),
	MERONYM_MEMBER ("%m", "Meronym, Member"),
	MERONYM_SUBSTANCE ("%s", "Meronym, Substance"),
	MERONYM_PART ("%p", "Meronym, Part"),
	ATTRIBUTE ("=", "Attribute"),
	DERIVED ("+", "Derived"),
	TOPIC (";c", "Topic"),
	TOPIC_MEMBER ("-c", "Topic Member"),
	REGION (";r", "Region"),
	REGION_MEMBER ("-r", "Region Member"),
	USAGE (";u", "Usage"),
	USAGE_MEMBER ("-u", "Usage Member"),
	ENTAILMENT ("*", "Entailment"),
	CAUSE (">", "Cause"),
	ALSO_SEE ("^", "Also See"),
	VERB_GROUP ("$", "Verb Group"),
	SIMILAR_TO ("&", "Similar To"),
	PARTICIPLE ("<", "Participle"),
	PERTAINYM ("\\", "Pertainym or Derived Adj");
	
	private final String fSymbol;
    private final String fName;

    /**
     * Private constructor because this is an enum type.
     */
    private WordnetPointerType(String symbol, String name) {
        fSymbol = symbol;
        fName = name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.item.IPointerType#getSymbol()
     */
    public String getSymbol() {
        return fSymbol;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.item.IPointerType#getName()
     */
    public String getName() {
        return fName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return fName.toLowerCase().replace(' ', '_');
    }

    /**
     * Static map to speed up access
     */
    private static Map<String, IPointerType> pointerMap = new HashMap<String, IPointerType>();

    static {
        WordnetPointerType[] values = WordnetPointerType.values();
        for (WordnetPointerType pointerType : values) {
            pointerMap.put(pointerType.getSymbol(), pointerType);
        }
    }

    /**
     * Returns the pointer type (static final instance) that matches the
     * specified pointer symbol.
     */
    public static IPointerType getPointerType(String symbol) {
        IPointerType pointerType = pointerMap.get(symbol);
        if (pointerType == null)
            throw new RuntimeException(
                    "No pointer type corresponding to symbol '" + pointerType
                            + "'");
        return pointerType;
    }
}
