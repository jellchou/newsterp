/******************************************************************************
 * Copyright (c) 2007 Mark Alan Finlayson
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT Java Wordnet Interface 
 * Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.mit.edu/~markaf/projects/wordnet/license.html.
 *****************************************************************************/

package edu.mit.jwi.content.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import edu.mit.jwi.content.MisformattedLineException;
import edu.mit.jwi.item.IPointerType;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IVerbFrame;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.PartOfSpeech;
import edu.mit.jwi.item.Synset;
import edu.mit.jwi.item.SynsetID;
import edu.mit.jwi.item.VerbFrame;
import edu.mit.jwi.item.Word;
import edu.mit.jwi.item.WordID;
import edu.mit.jwi.item.WordnetPointerType;

/**
 * Basic implementation of an object that takes a line from a Wordnet 2.1 or 3.0
 * data file (data.adv or adv.dat files, for example) and produces an
 * <tt>ISynset</tt> object.
 * 
 * @author Mark A. Finlayson
 * @version 1.1, 04/28/07
 * @since 1.5.0
 */
public class DataLineParser implements ILineParser<ISynset> {

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.file.ILineParser#parseIndexLine(java.lang.String)
     */
    public ISynset parseLine(String line) {
        if (line == null)
            throw new MisformattedLineException(line);

        try {
            ISynset result = null;

            StringTokenizer tokenizer = new StringTokenizer(line, " ");

            // Get offset
            long offset = Long.parseLong(tokenizer.nextToken());

            // Consume lex_filenum
            tokenizer.nextToken();

            // Get part of speech
            PartOfSpeech synset_pos;
            char synset_tag = tokenizer.nextToken().charAt(0);
            synset_pos = PartOfSpeech.getPartOfSpeech(synset_tag);

            ISynsetID synsetID = new SynsetID(offset, synset_pos);

            // Determine if it is an adjective satellite
            boolean isAdjectiveSatellite = false;
            if (synset_tag == 's') {
                isAdjectiveSatellite = true;
            }

            // Get word count
            int wordCount = Integer.parseInt(tokenizer.nextToken(), 16);

            // Get words
            String lemma, marker;
            WordProxy[] wordProxies = new WordProxy[wordCount];
            boolean isAdjectiveHead = false;
            for (int i = 0; i < wordCount; i++) {
                // consume next word
                lemma = tokenizer.nextToken();

                // if it is an adjective, a capitalized first character of
                // the first lemma means it is the head of an adjective cluster
                if (synset_pos == PartOfSpeech.ADJECTIVE & i == 0) {
                    if (Character.isUpperCase(lemma.charAt(0)))
                        isAdjectiveHead = true;
                }

                // if it is an adjective, it may be followed by a marker
                marker = null;
                if (synset_pos == PartOfSpeech.ADJECTIVE) {
                    if (lemma.lastIndexOf(')') == lemma.length() - 1) {
                        lemma = lemma.substring(0, lemma.length() - 1); // strip
                                                                        // final
                                                                        // ')'
                        marker = lemma.substring(lemma.lastIndexOf('(') + 1,
                                lemma.length()); // get marker
                        lemma = lemma.substring(0, lemma.lastIndexOf('(')); // strip
                                                                            // final
                                                                            // '('
                    }
                }

                // consume lex_id
                tokenizer.nextToken();

                wordProxies[i] = new WordProxy(synsetID, lemma, i + 1,
                        isAdjectiveHead, marker);
            }

            // Get pointer count
            int pointerCount = Integer.parseInt(tokenizer.nextToken());

            Map<IPointerType, List<ISynsetID>> synsetPointerMap = null;

            // Get pointers
            IPointerType pointer_type;
            long target_offset;
            PartOfSpeech target_pos;
            int source_target_num, source_num, target_num;
            List<ISynsetID> pointerList;
            IWordID target_word_id;
            ISynsetID target_synset_id;
            for (int i = 0; i < pointerCount; i++) {
                // get pointer symbol
                pointer_type = WordnetPointerType.getPointerType(tokenizer
                        .nextToken());

                // get synset target offset
                target_offset = Long.parseLong(tokenizer.nextToken());

                // get target synset pos
                target_pos = PartOfSpeech.getPartOfSpeech(tokenizer.nextToken()
                        .charAt(0));

                target_synset_id = new SynsetID(target_offset, target_pos);

                // get source/target numbers
                source_target_num = Integer.parseInt(tokenizer.nextToken(), 16);

                // this is a semantic pointer if the source/target numbers are
                // zero
                if (source_target_num == 0) {
                    if (synsetPointerMap == null)
                        synsetPointerMap = new HashMap<IPointerType, List<ISynsetID>>();
                    pointerList = synsetPointerMap.get(pointer_type);
                    if (pointerList == null) {
                        pointerList = new ArrayList<ISynsetID>();
                        synsetPointerMap.put(pointer_type, pointerList);
                    }
                    pointerList.add(target_synset_id);
                } else {
                    // this is a lexical pointer
                    source_num = source_target_num / 256;
                    target_num = source_target_num & 255;
                    target_word_id = new WordID(target_synset_id, target_num);
                    wordProxies[source_num - 1].addRelatedWord(pointer_type,
                            target_word_id);
                }
            }

            // Consume verb frames
            if (synset_pos == PartOfSpeech.VERB) {
                int frame_num, word_num;
                int verbFrameCount = Integer.parseInt(tokenizer.nextToken());
                IVerbFrame frame;
                for (int i = 0; i < verbFrameCount; i++) {
                    // Consume '+'
                    tokenizer.nextToken();
                    // Get frame number
                    frame_num = Integer.parseInt(tokenizer.nextToken());
                    frame = VerbFrame.getFrame(frame_num);
                    // Get word number
                    word_num = Integer.parseInt(tokenizer.nextToken(), 16);
                    if (word_num > 0) {
                        wordProxies[word_num - 1].addVerbFrame(frame);
                    } else {
                        for (WordProxy proxy : wordProxies) {
                            proxy.addVerbFrame(frame);
                        }
                    }
                }
            }

            // Get gloss
            String gloss = "";
            int index = line.indexOf('|');
            if (index > 0) {
                gloss = line.substring(index + 2).trim();
            }

            // create pointer and sysset id arrays
            IPointerType[] pointerArray = null;
            ISynsetID[][] synsetIDArray = null;
            if (synsetPointerMap != null) {
                pointerArray = synsetPointerMap.keySet().toArray(
                        new IPointerType[synsetPointerMap.size()]);
                synsetIDArray = new ISynsetID[synsetPointerMap.size()][];
                int i = 0;
                for (List<ISynsetID> synsets : synsetPointerMap.values()) {
                    synsetIDArray[i] = synsets.toArray(new ISynsetID[synsets
                            .size()]);
                    i++;
                }
            }

            // create word array
            IWord[] words = new IWord[wordProxies.length];
            for (int i = 0; i < wordProxies.length; i++) {
                wordProxies[i].setGloss(gloss);
                words[i] = wordProxies[i].instantiateWord();
            }

            result = new Synset(synsetID, pointerArray, synsetIDArray, words,
                    gloss, isAdjectiveHead, isAdjectiveSatellite);
            return result;
        } catch (NumberFormatException e) {
            throw new MisformattedLineException(line, e);
        } catch (NoSuchElementException e) {
            throw new MisformattedLineException(line, e);
        }
    }

    /**
     * This inner class is used to hold information about words before they are
     * instantiated.
     * 
     * @author Mark A. Finlayson
     * @version 1.00
     * @since 1.5.0
     */
    private class WordProxy {

        IWordID wordID;
        String lemma;
        String gloss = "";
        int number;
        boolean isAdjHead;
        String marker;
        Map<IPointerType, List<IWordID>> relatedWords;
        List<IVerbFrame> verbFrames;

        public WordProxy(ISynsetID synsetID, String lemma, int num,
                boolean isAdjHead, String marker) {
            wordID = new WordID(synsetID, num);
            this.lemma = lemma;
            this.isAdjHead = isAdjHead;
            this.marker = marker;
        }

        public void setGloss(String gloss) {
            this.gloss = gloss;
        }

        public void addRelatedWord(IPointerType type, IWordID word_id) {
            if (type == null | word_id == null)
                throw new IllegalArgumentException(
                        "Pointer type or word_id cannot be null");
            if (relatedWords == null)
                relatedWords = new HashMap<IPointerType, List<IWordID>>();
            List<IWordID> words = relatedWords.get(type);
            if (words == null) {
                words = new ArrayList<IWordID>();
                relatedWords.put(type, words);
            }
            words.add(word_id);
        }

        public void addVerbFrame(IVerbFrame frame) {
            if (verbFrames == null)
                verbFrames = new ArrayList<IVerbFrame>();
            verbFrames.add(frame);
        }

        public IWord instantiateWord() {

            IPointerType[] pointers = null;
            IWordID[][] relatedWordsArray = null;
            if (relatedWords != null) {
                pointers = relatedWords.keySet().toArray(
                        new IPointerType[relatedWords.size()]);
                int i = 0;
                relatedWordsArray = new IWordID[relatedWords.size()][];
                for (List<IWordID> words : relatedWords.values()) {
                    relatedWordsArray[i] = words.toArray(new IWordID[words
                            .size()]);
                    i++;
                }
            }

            IVerbFrame[] frames = null;
            if (verbFrames != null)
                frames = verbFrames.toArray(new IVerbFrame[verbFrames.size()]);

            return new Word(wordID, lemma, gloss, pointers, relatedWordsArray,
                    frames, marker);
        }

    }

}
