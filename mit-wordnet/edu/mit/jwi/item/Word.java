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
 * Default implementation of <tt>IWord</tt>
 * 
 * @author Mark A. Finlayson
 * @version 1.00, 4/13/06
 * @since 1.5.0
 */
public class Word implements IWord {

    private final IWordID fWordID;
    private final String fLemma;
    private final IVerbFrame[] fFrames;
    private final String fMarker;
    private final String fGloss;
    private final Map<IPointerType, IWordID[]> wordMap;

    /** To create a noun or adverb */
    public Word(ISynsetID synset_id, int number, String lemma, String gloss,
            IPointerType[] pointers, IWordID[][] relatedWords) {
        this(new WordID(synset_id, number), lemma, gloss, pointers,
                relatedWords);
    }

    /** To create a noun or adverb */
    public Word(IWordID id, String lemma, String gloss,
            IPointerType[] pointers, IWordID[][] relatedWords) {
        this(id, lemma, gloss, pointers, relatedWords, null, null);
    }

    /** To create a verb */
    public Word(ISynsetID synset_id, int number, String lemma, String gloss,
            IPointerType[] pointers, IWordID[][] relatedWords,
            IVerbFrame[] frames) {
        this(new WordID(synset_id, number), lemma, gloss, pointers,
                relatedWords, frames);
    }

    /** To create a verb */
    public Word(IWordID id, String lemma, String gloss,
            IPointerType[] pointers, IWordID[][] relatedWords,
            IVerbFrame[] frames) {
        this(id, lemma, gloss, pointers, relatedWords, frames, null);
    }

    /** To create an adjective */
    public Word(ISynsetID synset_id, int number, String lemma, String gloss,
            IPointerType[] pointers, IWordID[][] relatedWords, String marker) {
        this(new WordID(synset_id, number), lemma, gloss, pointers,
                relatedWords, marker);
    }

    /** To create an adjective */
    public Word(IWordID id, String lemma, String gloss,
            IPointerType[] pointers, IWordID[][] relatedWords, String marker) {
        this(id, lemma, gloss, pointers, relatedWords, null, marker);
    }

    /** Full control */
    public Word(ISynsetID synset_id, int number, String lemma, String gloss,
            IPointerType[] pointers, IWordID[][] relatedWords,
            IVerbFrame[] frames, String marker) {
        this(new WordID(synset_id, number), lemma, gloss, pointers,
                relatedWords, frames, marker);
    }

    /** Full control */
    public Word(IWordID id, String lemma, String gloss,
            IPointerType[] pointers, IWordID[][] relatedWords,
            IVerbFrame[] frames, String marker) {
        fWordID = id;
        fLemma = lemma;
        fMarker = marker;
        fGloss = gloss;

        // fill synset map
        if (pointers == null & relatedWords == null) {
            wordMap = null;
        } else if (pointers == null | relatedWords == null) {
            throw new IllegalArgumentException(
                    "Pointer or id array (but not both) is null: this is not allowed");
        } else if (pointers.length != relatedWords.length) {
            throw new IllegalArgumentException(
                    "Pointer and id array are not of the same length");
        } else {
            wordMap = new HashMap<IPointerType, IWordID[]>();
            for (int i = 0; i < pointers.length; i++) {
                wordMap.put(pointers[i], relatedWords[i]);
            }
        }

        if (frames == null) {
            fFrames = null;
        } else {
            fFrames = new IVerbFrame[frames.length];
            System.arraycopy(frames, 0, fFrames, 0, frames.length);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.data.IWord#getLemma()
     */
    public String getLemma() {
        return fLemma;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.data.IWord#getPartOfSpeech()
     */
    public PartOfSpeech getPartOfSpeech() {
        return fWordID.getSynsetID().getPartOfSpeech();
    }

    /* (non-Javadoc) @see edu.mit.wordnet.item.IHasID#getID() */
    public IWordID getID() {
        return fWordID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.data.IWord#getSynsetID()
     */
    public ISynsetID getSynsetID() {
        return fWordID.getSynsetID();
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.item.IWord#getGloss()
     */
    public String getGloss() {
        return fGloss;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.data.IWord#getRelatedWords(edu.mit.wordnet.core.data.IPointerType)
     */
    public IWordID[] getRelatedWords(IPointerType type) {
        if (wordMap == null) return new IWordID[0];
        IWordID[] array = wordMap.get(type);
        IWordID[] result = new IWordID[array.length];
        System.arraycopy(array, 0, result, 0, array.length);
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.data.IWord#getAllRelatedWords()
     */
    public IWordID[] getAllRelatedWords() {
        if (wordMap == null) return new IWordID[0];
        Set<IWordID> uniqueWords = new HashSet<IWordID>();
        for (IWordID[] ids : wordMap.values()) {
            uniqueWords.addAll(Arrays.asList(ids));
        }
        return uniqueWords.toArray(new IWordID[uniqueWords.size()]);
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.data.IWord#getVerbFrames()
     */
    public IVerbFrame[] getVerbFrames() {
        if (fWordID.getPartOfSpeech() != PartOfSpeech.VERB) return null;
        IVerbFrame[] result = new IVerbFrame[fFrames.length];
        System.arraycopy(fFrames, 0, result, 0, fFrames.length);
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.data.IWord#getAdjectiveMarker()
     */
    public String getAdjectiveMarker() {
        if (fWordID.getPartOfSpeech() != PartOfSpeech.ADJECTIVE) return null;
        return fMarker;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        if (fWordID.getNumber() == 0) {
            return "W-" + fWordID.getSynsetID().toString().substring(4) + "-?-"
                    + fLemma;
        } else {
            return "W-" + fWordID.getSynsetID().toString().substring(4) + "-"
                    + fWordID.getNumber() + "-" + fLemma;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + Arrays.hashCode(fFrames);
        result = PRIME * result + ((fMarker == null) ? 0 : fMarker.hashCode());
        result = PRIME * result + ((fWordID == null) ? 0 : fWordID.hashCode());
        result = PRIME * result + ((wordMap == null) ? 0 : wordMap.hashCode());
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
        final Word other = (Word) obj;
        if (!Arrays.equals(fFrames, other.fFrames)) return false;
        if (fMarker == null) {
            if (other.fMarker != null) return false;
        } else if (!fMarker.equals(other.fMarker)) return false;
        if (fWordID == null) {
            if (other.fWordID != null) return false;
        } else if (!fWordID.equals(other.fWordID)) return false;
        if (wordMap == null) {
            if (other.wordMap != null) return false;
        } else if (!wordMap.equals(other.wordMap)) return false;
        return true;
    }

}
