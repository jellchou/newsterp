/******************************************************************************
 * Copyright (c) 2007 Mark Alan Finlayson
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT Java Wordnet Interface 
 * Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.mit.edu/~markaf/projects/wordnet/license.html.
 *****************************************************************************/

package edu.mit.jwi.morph;

import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

import edu.mit.jwi.dict.IDictionary;
import edu.mit.jwi.item.IExceptionEntryProxy;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.PartOfSpeech;

/**
 * This stemmer adds functionality to the simple pattern-based stemmer
 * <tt>SimpleStemmer</tt> by checking to see if possible stems are actually
 * contained in Wordnet. If any stems are found, only these stems are returned.
 * If no prospective stems are found, the word is considered 'unknown', and the
 * result returned is the same as that of the <tt>SimpleStemmer</tt> class.
 * 
 * @author Mark A. Finlayson
 * @version 1.00, 4/13/07
 * @since 1.5.0
 */
public class WordnetStemmer extends SimpleStemmer {

    /**
     *  The dictionary
     */
    IDictionary fDictionary;

    /**
     * Constructs a WordnetStemmer that, naturally, requires a Wordnet
     * dictionary.
     * 
     * @param dictionary
     */
    public WordnetStemmer(IDictionary dictionary) {
        assert dictionary != null;
        fDictionary = dictionary;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.morph.SimpleStemmer#getRoots(java.lang.String)
     */
    public SortedSet<String> getRoots(String word) {

        SortedSet<String> result = null;
        SortedSet<String> roots;

        for (PartOfSpeech pos : PartOfSpeech.values()) {
            roots = getRoots(word, pos);
            if (result == null & roots != null)
                result = new TreeSet<String>();
            if (roots != null)
                result.addAll(roots);
        }

        return result;

    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.morph.SimpleStemmer#getRoots(java.lang.String,
     *      edu.mit.wordnet.data.PartOfSpeech)
     */
    public SortedSet<String> getRoots(String word, PartOfSpeech pos) {

        if (pos == null)
            return getRoots(word);

        SortedSet<String> result = null;

        // first look and see if it's in Wordnet...if so, it's a stem
        if (fDictionary.getIndexWord(word, pos) != null) {
            result = new TreeSet<String>();
            result.add(word);
            return result;
        }

        // if not in wordnet already, look for it in exception lists
        IExceptionEntryProxy entry = fDictionary.getExceptionEntry(word, pos);
        if (entry != null)
            return new TreeSet<String>(Arrays.asList(entry.getRootForms()));

        // now try simple stemmer and look for those roots in wordnet
        SortedSet<String> possibles = super.getRoots(word, pos);
        if (possibles == null)
            possibles = new TreeSet<String>();
        possibles.add(word);

        IIndexWord idxWord;
        for (String possible : possibles) {
            idxWord = fDictionary.getIndexWord(possible, pos);
            if (idxWord != null) {
                if (result == null)
                    result = new TreeSet<String>();
                result.add(possible);
            }
        }

        if (result != null)
            return result;

        // it was found no where, so just return the results of the simple stemmer
        return possibles;
    }
}
