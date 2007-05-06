/******************************************************************************
 * Copyright (c) 2007 Mark Alan Finlayson
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT Java Wordnet Interface 
 * Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.mit.edu/~markaf/projects/wordnet/license.html.
 *****************************************************************************/

package edu.mit.jwi.content.parse;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import edu.mit.jwi.content.MisformattedLineException;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.IndexWord;
import edu.mit.jwi.item.PartOfSpeech;
import edu.mit.jwi.item.SynsetID;
import edu.mit.jwi.item.WordID;


/**
 * Basic implementation of an <tt>ILineParser</tt> that takes a line from a
 * Wordnet 2.1 index file (idx.adv or adv.idx files, for example) and
 * produces an <tt>IIndexWord</tt> object.
 * 
 * @author Mark A. Finlayson
 * @version 1.1, 04/28/07
 * @since 1.5.0
 */
public class IndexLineParser implements ILineParser<IIndexWord> {
	
	/* (non-Javadoc) @see edu.mit.wordnet.core.file.ILineParser#parseIndexLine(java.lang.String) */
	public IIndexWord parseLine(String line) {
		if(line == null) throw new MisformattedLineException(line);
		
        try {
        	IIndexWord result = null;
			StringTokenizer tokenizer = new StringTokenizer(line, " ");
			
			// get lemma
			String lemma = tokenizer.nextToken();
			
			// get pos
			PartOfSpeech partOfSpeech;
			String pos = tokenizer.nextToken();
			if(pos.length() == 1){
				partOfSpeech = PartOfSpeech.getPartOfSpeech(pos.charAt(0));
			} else {
				throw new MisformattedLineException(line);
			}
			
			// consume p_cnt
			tokenizer.nextToken();
			
			// consume ptr_symbols
			int pointerCount = Integer.parseInt(tokenizer.nextToken());
			for (int i = 0; i < pointerCount; ++i) tokenizer.nextToken();
			
			// get sense_cnt
			int senseCount = Integer.parseInt(tokenizer.nextToken());

			// consume tagged sense count
			tokenizer.nextToken(); 

			// get words
			IWordID[] words = new IWordID[senseCount];
			long offset;
			for (int i = 0; i < senseCount; i++) {
			    offset = Long.parseLong(tokenizer.nextToken());
			    words[i] = new WordID(new SynsetID(offset, partOfSpeech), lemma);
			}
			
			result = new IndexWord(lemma, partOfSpeech, words);
			return result;
		} catch (NumberFormatException e) {
			throw new MisformattedLineException(line, e);
		} catch (NoSuchElementException e){
			throw new MisformattedLineException(line, e);
		}
	}
}
