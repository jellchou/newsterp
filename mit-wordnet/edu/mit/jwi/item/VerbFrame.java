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
 * Default, enum, implementation of <tt>IVerbFrame</tt> that does not read
 * from the actual Wordnet database. This is for Wordnet 2.1 & 3.0.
 * 
 * @author Mark A. Finlayson
 * @version 1.00, 4/13/06
 * @since 1.5.0
 */
public enum VerbFrame implements IVerbFrame {
	
	NUM_01 (1, "Something ----s"),
	NUM_02 (2, "Somebody ----s"),
	NUM_03 (3, "It is ----ing"),
	NUM_04 (4, "Something is ----ing PP"),
	NUM_05 (5, "Something ----s something Adjective/Noun"),
	NUM_06 (6, "Something ----s Adjective/Noun"),
	NUM_07 (7, "Somebody ----s Adjective"),
	NUM_08 (8, "Somebody ----s something"),
	NUM_09 (9, "Somebody ----s somebody"),
	NUM_10 (10, "Something ----s somebody"),
	NUM_11 (11, "Something ----s something"),
	NUM_12 (12, "Something ----s to somebody"),
	NUM_13 (13, "Somebody ----s on something"),
	NUM_14 (14, "Somebody ----s somebody something"),
	NUM_15 (15, "Somebody ----s something to somebody"),
	NUM_16 (16, "Somebody ----s something from somebody"),
	NUM_17 (17, "Somebody ----s somebody with something"),
	NUM_18 (18, "Somebody ----s somebody of something"),
	NUM_19 (19, "Somebody ----s something on somebody"),
	NUM_20 (20, "Somebody ----s somebody PP"),
	NUM_21 (21, "Somebody ----s something PP"),
	NUM_22 (22, "Somebody ----s PP"),
	NUM_23 (23, "Somebody's (body part) ----s"),
	NUM_24 (24, "Somebody ----s somebody to INFINITIVE"),
	NUM_25 (25, "Somebody ----s somebody INFINITIVE"),
	NUM_26 (26, "Somebody ----s that CLAUSE"),
	NUM_27 (27, "Somebody ----s to somebody"),
	NUM_28 (28, "Somebody ----s to INFINITIVE"),
	NUM_29 (29, "Somebody ----s whether INFINITIVE"),
	NUM_30 (30, "Somebody ----s somebody into V-ing something"),
	NUM_31 (31, "Somebody ----s something with something"),
	NUM_32 (32, "Somebody ----s INFINITIVE"),
	NUM_33 (33, "Somebody ----s VERB-ing"),
	NUM_34 (34, "It ----s that CLAUSE"),
	NUM_35 (35, "Something ----s INFINITIVE");

	private final int fNumber;
	private final String fTemplate;
	
	/**
	 * Private constructor because this is an enum
	 */
	private VerbFrame(int number, String template){
		fNumber = number;
		fTemplate = template; 
	}
	
	/* (non-Javadoc) @see edu.mit.wordnet.item.IVerbFrame#getNumber() */
	public int getNumber(){
		return fNumber;
	}
	
	/* (non-Javadoc) @see edu.mit.wordnet.item.IVerbFrame#getTemplate() */
	public String getTemplate(){
		return fTemplate;
	}
	
	/* (non-Javadoc) @see edu.mit.wordnet.item.IVerbFrame#instantiateTemplate(java.lang.String) */
	public String instantiateTemplate(String verb){
		int index = fTemplate.indexOf("----");
		if (index == -1) return "";
		return fTemplate.substring(0, index) + verb + fTemplate.substring(index + 5, fTemplate.length());
	}
	
	/* (non-Javadoc) @see java.lang.Object#toString() */
	public String toString(){
		return "[" + fNumber + " : " + fTemplate + " ]";
	}
	
	/**
	 * Static map to speed up accesses of content types
	 */
	private static Map<Integer, IVerbFrame> frameMap = new HashMap<Integer, IVerbFrame>();
	
	static {
		VerbFrame[] values = VerbFrame.values();
		for(VerbFrame frame : values){
			frameMap.put(frame.getNumber(), frame);
		}
	}

	/**
     * Returns the verb frame for the specified number.
     */
    public static IVerbFrame getFrame(int number) {
        IVerbFrame frame = frameMap.get(number);
        if (frame == null)
            throw new RuntimeException(
                    "No verb frame corresponding to number '" + number + "'");
        return frame;
    }

}
