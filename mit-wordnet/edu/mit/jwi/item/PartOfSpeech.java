/******************************************************************************
 * Copyright (c) 2007 Mark Alan Finlayson
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT Java Wordnet Interface 
 * Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.mit.edu/~markaf/projects/wordnet/license.html.
 *****************************************************************************/

package edu.mit.jwi.item;

/**
 * Represents part of speech objects.
 * 
 * @author Mark A. Finlayson
 * @version 1.00, 4/12/06
 * @since 1.5.0
 */
public enum PartOfSpeech {

    NOUN("noun", new String[] { "noun" }, 'n'), VERB("verb",
            new String[] { "verb" }, 'v'), ADJECTIVE("adjective",
            new String[] { "adj" }, 'a'), ADVERB("adverb",
            new String[] { "adv" }, 'r');

    String fName;
    String[] fHints;
    char fTag;

    /**
     * Private constructor because it is an enum object.
     */
    private PartOfSpeech(String toString, String[] patterns, char tag) {
        fName = toString;
        fHints = patterns;
        fTag = tag;
    }

    /**
     * Returns a set of strings that can be used to identify resource
     * corresponding to objects with this part of speech.
     * 
     * @return String[]
     */
    public String[] getResourceNameHints() {
        return fHints;
    }

    /**
     * The tag that is used to indicate this part of speech in Wordnet data
     * files
     * 
     * @return char
     */
    public char getTag() {
        return fTag;
    }

    /**
     * Sets the user-friendly name of this part of speech.
     */
    public void setName(String name) {
        fName = name;
    }

    /**
     * Sets the resource hints. See {@link #getResourceNameHints()}
     */
    public void setResourceNameHints(String[] hints) {
        fHints = hints;
    }

    /**
     * Sets the part of speech tag. See {@link #getTag()}
     */
    public void setTag(char tag) {
        fTag = tag;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return fName;
    }

    /**
     * A convience method that allows retrieval of the part of speech object
     * given the tag.
     * 
     * @return PartOfSpeech the part of speech object corresponding to the
     *         specified tag, or null if none is found
     */
    public static PartOfSpeech getPartOfSpeech(char tag) {
        // special case, 's' for adjective satellite
        if (tag == 's') return ADJECTIVE;
        for (PartOfSpeech pos : PartOfSpeech.values()) {
            if (pos.getTag() == tag) return pos;
        }
        return null;
    }
}
