/******************************************************************************
 * Copyright (c) 2007 Mark Alan Finlayson
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT Java Wordnet Interface 
 * Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.mit.edu/~markaf/projects/wordnet/license.html.
 *****************************************************************************/

package edu.mit.jwi.item;

import java.util.Arrays;

/**
 * Default implementation of <tt>IIndexWord</tt>.
 * 
 * @author Mark A. Finlayson
 * @version 1.00, 4/13/06
 * @since 1.5.0
 */
public class IndexWord implements IIndexWord {

    private final IIndexWordID fID;
    private final IWordID[] fWordIDs;

    /**
     * The arguments, and the contents of the array, cannot be null, otherwise
     * the constructor throws an <tt>IllegalArgumentException</tt>
     */
    public IndexWord(String lemma, PartOfSpeech pos, IWordID[] words) {
        this(new IndexWordID(lemma, pos), words);
    }

    /**
     * The arguments, and the contents of the array, cannot be null, otherwise
     * the constructor throws an <tt>IllegalArgumentException</tt>
     */
    public IndexWord(IIndexWordID id, IWordID[] words) {
        if (id == null)
            throw new IllegalArgumentException(
                    "IIndexWordID cannot be null for IndexWord constructor");
        if (words == null)
            throw new IllegalArgumentException(
                    "IWordID array cannot be null for IndexWord constructor");
        if (words.length == 0)
            throw new IllegalArgumentException(
                    "IWordID array cannot be empty for IndexWord constructor");
        fID = id;
        fWordIDs = new IWordID[words.length];
        System.arraycopy(words, 0, fWordIDs, 0, words.length);
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.data.IIndexWord#getLemma()
     */
    public String getLemma() {
        return fID.getLemma();
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.item.IIndexWord#getWordIDs()
     */
    public IWordID[] getWordIDs() {
        IWordID[] result = new IWordID[fWordIDs.length];
        System.arraycopy(fWordIDs, 0, result, 0, fWordIDs.length);
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.item.IHasID#getID()
     */
    public IIndexWordID getID() {
        return fID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.data.IIndexWord#getPartOfSpeech()
     */
    public PartOfSpeech getPartOfSpeech() {
        return fID.getPartOfSpeech();
    }

    public String toString() {
        String result = "[" + fID.getLemma() + " (" + fID.getPartOfSpeech()
                + ") ";
        for (IWordID id : fWordIDs) {
            result = result + id.toString() + ", ";
        }
        result = result.substring(0, result.length() - 2) + "]";
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((fID == null) ? 0 : fID.hashCode());
        result = PRIME * result + Arrays.hashCode(fWordIDs);
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
        final IndexWord other = (IndexWord) obj;
        if (fID == null) {
            if (other.fID != null) return false;
        } else if (!fID.equals(other.fID)) return false;
        if (!Arrays.equals(fWordIDs, other.fWordIDs)) return false;
        return true;
    }

}
