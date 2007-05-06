/******************************************************************************
 * Copyright (c) 2007 Mark Alan Finlayson
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT Java Wordnet Interface 
 * Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.mit.edu/~markaf/projects/wordnet/license.html.
 *****************************************************************************/

package edu.mit.jwi.dict;

import java.util.Iterator;

import edu.mit.jwi.content.IParserProvider;
import edu.mit.jwi.data.IDataProvider;
import edu.mit.jwi.item.IExceptionEntry;
import edu.mit.jwi.item.IExceptionEntryID;
import edu.mit.jwi.item.IExceptionEntryProxy;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.IIndexWordID;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.PartOfSpeech;

/**
 * Objects that implement this interface are intended as the main entry point to
 * accessing the dictionary data. The dictionary must be opened by calling
 * <tt>open()</tt> before it is used. The dictionary allows the retrieval of
 * four different types of objects: those that implement
 * {@link edu.mit.jwi.item.IIndexWord} {@link edu.mit.jwi.item.IWord},
 * {@link edu.mit.jwi.item.ISynset}, and
 * {@link edu.mit.jwi.item.IExceptionEntry}. These operations are achieved
 * by constructing the appropriate ID object (that contains the minimum required
 * information to retrieve the said object) and passing it to the appropriate
 * method.
 * <p>
 * The current version is programmed with the Wordnet 2.1/3.0 specification in mind.
 * Wordnet can be found at <a
 * href="http://wordnet.princeton.edu/">http://wordnet.princeton.edu/</a>.
 * <p>
 * A number of interfaces are provided to assist in customizing
 * dictionary behavior. The main ones are
 * {@link edu.mit.jwi.data.IDataProvider} and
 * {@link edu.mit.jwi.content.IParserProvider}. <tt>IFileProvider</tt>
 * specifies methods that are used to create the objects that control the
 * dictionary's access to lines in the data files. <tt>IParserProvider</tt>
 * controls how the dictionary interprets the data in a particular line to
 * create the objects it returns.
 * 
 * @author Mark A. Finlayson
 * @version 1.1, 4/28/07
 * @since 1.5
 */
public interface IDictionary {

	/**
	 * This opens this dictionary by instantiating objects corresponding to the
	 * data backing, such as files or socket connections. This method should be
	 * called before the dictionary is used.
	 * <p>
	 * This method is also be responsible for populating any fast-access maps
	 * that allow the other methods to easily find the right IDictionaryFile
	 * instance for their operations.
	 * 
	 * @return <code>true</code> if there were no errors creating data backing
	 *         objects; <code>false</code> otherwise.
	 */
	public boolean open();

	/**
	 * This closes the dictionary by disposing of data backing objects or
	 * connections. It should not be irreversible, though: another call to
	 * open() should reopen the dictionary.
	 */
	public void close();

	/**
	 * @return <code>true</code> if the dictionary is open, that is, ready to
	 *         accept queries; returns <code>false</code> otherwise
	 */
	public boolean isOpen();
	
    /**
	 * Sets the data provider. If the dictionary is open, calling this method
	 * will throw 
	 * 
	 * @param provider
	 *            the provider that provides lines from Wordnet data files
	 */
    public void setDataProvider(IDataProvider provider);
    
    /**
	 * Sets the parser provider and refreshes the individual line parsers. If
	 * <code>null</code> is passed to this method, all the individual parsers
	 * will also be set to <code>null</code> and the dictionary will not
	 * produce any new data.
	 * 
	 * @param provider
	 *            The provider that will provide the individual line parsers
	 */
    public void setParserProvider(IParserProvider provider);
    
    /**
     * @return the data provider for this dictionary
     */
    public IDataProvider getDataProvider();
    
    /** 
     * @return the parser provider for this dictionary
     */
    public IParserProvider getParserProvider();

	/**
	 * This method should be identical to
	 * <code>getIndexWord(IIndexWordID)</code> and is provided as a
	 * convenience.
	 */
	public IIndexWord getIndexWord(String lemma, PartOfSpeech pos);

	/**
	 * Fetches the specified index word object from the database. If the
	 * specified lemma/pos combination is not found, returns <tt>null</tt>.
	 */
	public IIndexWord getIndexWord(IIndexWordID id);

	/**
	 * Fetches the word from the database, as specified by the indicated IWordID
	 * object. If the specified id is not found, returns <tt>null</tt>
	 */
	public IWord getWord(IWordID id);

	/**
	 * Fetches the synset from the database, as specified by the indicated
	 * ISynsetID object. If the specified id is not found, returns <tt>null</tt>
	 */
	public ISynset getSynset(ISynsetID id);

	/**
	 * This method should be identical to {@link IDictionary#getExceptionEntry(IExceptionEntryID)},
	 * and is provided as a convenience.
	 */
	public IExceptionEntry getExceptionEntry(String surfaceForm, PartOfSpeech pos);

	/**
	 * Fetches the exception entry from the database, as specified by the
	 * indicated ISynsetID object. If the specified id is not found, returns
	 * <tt>null</tt>
	 */
	public IExceptionEntry getExceptionEntry(IExceptionEntryID id);

	/**
	 * Returns an iterator that will iterate over all index words of the
	 * specified part of speech.
	 */
	public Iterator<IIndexWord> getIndexWordIterator(PartOfSpeech pos);

	/**
	 * Returns an iterator that will iterate over all index words of the
	 * specified part of speech whose lemmas match the specified pattern. The
	 * wildcards are allowed, and what constitutes a 'match' is implementation
	 * dependent.
	 */
	public Iterator<IIndexWord> getIndexWordPatternIterator(PartOfSpeech pos, String pattern);

	/**
	 * Returns an iterator that will iterate over all synsets of the specified
	 * part of speech.
	 */
	public Iterator<ISynset> getSynsetIterator(PartOfSpeech pos);

	/**
	 * Returns an iterator that will iterate over all exception entries of the
	 * specified part of speech.
	 */
	public Iterator<IExceptionEntryProxy> getExceptionEntryIterator(PartOfSpeech pos);

}
