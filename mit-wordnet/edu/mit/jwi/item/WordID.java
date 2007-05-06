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
 * Default implementation of <tt>IWordID</tt>
 * 
 * @author Mark A. Finlayson
 * @version 1.00, 4/13/06
 * @since 1.5.0
 */
public class WordID implements IWordID {

    private final ISynsetID sID;
    private final int fNum;
    private final String fLemma;

    /**
     * The synset id cannot be null, otherwise the constructor throws an
     * <tt>IllegalArgumentException</tt>
     */
    public WordID(ISynsetID synsetID, int number) {
        if (synsetID == null)
            throw new IllegalArgumentException(
                    "Arguments cannot be null for WordID constructor");
        sID = synsetID;
        fNum = number;
        fLemma = null;
    }

    /**
     * The arguments cannot be null or empty, otherwise the constructor throws
     * an <tt>IllegalArgumentException</tt>
     */
    public WordID(ISynsetID synsetID, String lemma) {
        if (synsetID == null | lemma == null)
            throw new IllegalArgumentException(
                    "Arguments cannot be null for WordID constructor");
        if (lemma.equals(""))
            throw new IllegalArgumentException(
                    "Lemma cannot be empty for WordID constructor");
        sID = synsetID;
        fNum = -1;
        fLemma = lemma;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.data.IWordID#getSynsetID()
     */
    public ISynsetID getSynsetID() {
        return sID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.data.IWordID#getNumber()
     */
    public int getNumber() {
        return fNum;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.item.IWordID#getLemma()
     */
    public String getLemma() {
        return fLemma;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.data.IWordID#getPartOfSpeech()
     */
    public PartOfSpeech getPartOfSpeech() {
        return sID.getPartOfSpeech();
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.item.IItemID#getIdentifiedClass()
     */
    public Class<IWord> getIdentifiedClass() {
        return IWord.class;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((sID == null) ? 0 : sID.hashCode());
        result = PRIME * result + fNum;
        if (fLemma != null) result = PRIME * result + fLemma.hashCode();
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final WordID other = (WordID) obj;
        if (sID == null) {
            if (other.sID != null) return false;
        } else if (!sID.equals(other.sID)) return false;
        if (other.fNum != 0 & fNum != 0 & other.fNum != fNum) return false;
        if (other.fLemma != null & fLemma != null) {
            if (!other.fLemma.equals(fLemma)) return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        if (fNum < 0) {
            return "WID-" + sID.toString().substring(4) + "-?-" + fLemma;
        } else {
            return "WID-" + sID.toString().substring(4) + "-" + fNum + "-?";
        }
    }

    /**
     * Convenience method for transforming the result of the {@link #toString()}
     * method back into an <tt>WordID</tt>
     * 
     * @return WordID The parsed id, or null if the string is malformed
     */
    public static IWordID parseWordID(String value) {
        if (value == null) return null;
        if (!value.startsWith("WID-")) return null;
        int begin, end;

        // Get SID
        begin = 4;
        end = value.indexOf('-', 4);
        if (end < begin) return null;
        end = value.indexOf('-', end + 1);
        if (end < begin) return null;
        ISynsetID synsetID = SynsetID.parseSynsetID("SID-"
                + value.substring(begin, end));
        if (synsetID == null) return null;

        // Get Num, it exists
        begin = end + 1;
        end = value.indexOf('-', begin);
        if (end < begin) return null;
        int num = -1;
        if (value.charAt(begin) != '?') {
            try {
                num = Integer.parseInt(value.substring(begin, end));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        if (num < -1) return null;
        if (num > -1) return new WordID(synsetID, num);

        // Try to get lemma now
        String lemma = value.substring(end + 1);
        if (lemma.equals("?") | lemma.length() == 0) return null;

        return new WordID(synsetID, lemma);
    }
}
