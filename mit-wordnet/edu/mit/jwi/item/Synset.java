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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Default implementation of <tt>ISynset</tt>
 * 
 * @author Mark A. Finlayson
 * @version 1.00, 4/13/06
 * @since 1.5.0
 */
public class Synset implements ISynset {

    final ISynsetID fID;
    final String fGloss;
    final boolean isAdjHead;
    final boolean isAdjSat;
    final Map<IPointerType, ISynsetID[]> synsetMap;
    final IWord[] fWords;

    public Synset(long offset, PartOfSpeech pos, IPointerType[] pointers,
            ISynsetID[][] ids, IWord[] words, String gloss) {
        this(new SynsetID(offset, pos), pointers, ids, words, gloss);
    }

    public Synset(ISynsetID id, IPointerType[] pointers, ISynsetID[][] ids,
            IWord[] words, String gloss) {
        this(id, pointers, ids, words, gloss, false, false);
    }

    public Synset(long offset, PartOfSpeech pos, IPointerType[] pointers,
            ISynsetID[][] ids, IWord[] words, String gloss, boolean adjHead,
            boolean adjSat) {
        this(new SynsetID(offset, pos), pointers, ids, words, gloss, adjHead,
                adjSat);

    }

    public Synset(ISynsetID id, IPointerType[] pointers, ISynsetID[][] ids,
            IWord[] words, String gloss, boolean adjHead, boolean adjSat) {
        if (words == null)
            throw new IllegalArgumentException(
                    "Words array cannot be null for Synset constructor");

        fID = id;
        fGloss = gloss;
        isAdjHead = adjHead;
        isAdjSat = adjSat;

        // copy words into array
        fWords = new IWord[words.length];
        System.arraycopy(words, 0, fWords, 0, words.length);

        // fill synset map
        if (pointers == null & ids == null) {
            synsetMap = null;
        } else if (pointers == null | ids == null) {
            throw new IllegalArgumentException(
                    "Pointer or id array (but not both) is null: this is not allowed");
        } else if (pointers.length != ids.length) {
            throw new IllegalArgumentException(
                    "Pointer and id array are not of the same length");
        } else {
            synsetMap = new HashMap<IPointerType, ISynsetID[]>();
            for (int i = 0; i < pointers.length; i++) {
                synsetMap.put(pointers[i], ids[i]);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.item.IHasID#getID()
     */
    public ISynsetID getID() {
        return fID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.item.ISynset#getOffset()
     */
    public long getOffset() {
        return fID.getOffset();
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.data.ISynset#getPartOfSpeech()
     */
    public PartOfSpeech getPartOfSpeech() {
        return fID.getPartOfSpeech();
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.data.ISynset#getGloss()
     */
    public String getGloss() {
        return fGloss;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.data.ISynset#getWords()
     */
    public IWord[] getWords() {
        IWord[] result = new IWord[fWords.length];
        System.arraycopy(fWords, 0, result, 0, fWords.length);
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.data.ISynset#getRelatedSynsets(edu.mit.wordnet.core.data.IPointerType)
     */
    public ISynsetID[] getRelatedSynsets(IPointerType type) {
        if (synsetMap == null) return new ISynsetID[0];
        ISynsetID[] array = synsetMap.get(type);
        if (array == null) return new ISynsetID[0];
        ISynsetID[] result = new ISynsetID[array.length];
        System.arraycopy(array, 0, result, 0, array.length);
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.data.ISynset#getAllRelatedSynsets()
     */
    public ISynsetID[] getRelatedSynsets() {
        if (synsetMap == null) return new ISynsetID[0];
        Set<ISynsetID> uniqueSynsets = new HashSet<ISynsetID>();
        for (ISynsetID[] ids : synsetMap.values()) {
            uniqueSynsets.addAll(Arrays.asList(ids));
        }
        return uniqueSynsets.toArray(new ISynsetID[uniqueSynsets.size()]);
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.data.ISynset#isAdjectiveSatellite()
     */
    public boolean isAdjectiveSatellite() {
        if (fID.getPartOfSpeech() != PartOfSpeech.ADJECTIVE) return false;
        return isAdjHead;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.data.ISynset#isAdjectiveHead()
     */
    public boolean isAdjectiveHead() {
        if (fID.getPartOfSpeech() != PartOfSpeech.ADJECTIVE) return false;
        return isAdjSat;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((fGloss == null) ? 0 : fGloss.hashCode());
        result = PRIME * result + ((fID == null) ? 0 : fID.hashCode());
        result = PRIME * result + Arrays.hashCode(fWords);
        result = PRIME * result + (isAdjHead ? 1231 : 1237);
        result = PRIME * result + (isAdjSat ? 1231 : 1237);
        result = PRIME * result
                + ((synsetMap == null) ? 0 : synsetMap.hashCode());
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
        final Synset other = (Synset) obj;
        if (fGloss == null) {
            if (other.fGloss != null) return false;
        } else if (!fGloss.equals(other.fGloss)) return false;
        if (fID == null) {
            if (other.fID != null) return false;
        } else if (!fID.equals(other.fID)) return false;
        if (!Arrays.equals(fWords, other.fWords)) return false;
        if (isAdjHead != other.isAdjHead) return false;
        if (isAdjSat != other.isAdjSat) return false;
        if (synsetMap == null) {
            if (other.synsetMap != null) return false;
        } else if (!synsetMap.equals(other.synsetMap)) return false;
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("SYNSET{");
        buffer.append(fID.toString());
        buffer.append(" : Words[");
        for (IWord word : fWords) {
            buffer.append(word.toString());
            buffer.append(", ");
        }
        buffer.replace(buffer.length() - 2, buffer.length(), "]}");

        return buffer.toString();
    }

}
