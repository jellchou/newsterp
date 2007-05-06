/******************************************************************************
 * Copyright (c) 2007 Mark Alan Finlayson
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT Java Wordnet Interface 
 * Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.mit.edu/~markaf/projects/wordnet/license.html.
 *****************************************************************************/

package edu.mit.jwi.dict;

import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.Iterator;

import edu.mit.jwi.content.IContentType;
import edu.mit.jwi.content.IParserProvider;
import edu.mit.jwi.content.WordnetContentType;
import edu.mit.jwi.content.parse.ILineParser;
import edu.mit.jwi.content.parse.ParserProvider;
import edu.mit.jwi.data.FileProvider;
import edu.mit.jwi.data.IDataProvider;
import edu.mit.jwi.data.IDictionaryDataSource;
import edu.mit.jwi.data.WordnetDataType;
import edu.mit.jwi.item.ExceptionEntry;
import edu.mit.jwi.item.ExceptionEntryID;
import edu.mit.jwi.item.IExceptionEntry;
import edu.mit.jwi.item.IExceptionEntryID;
import edu.mit.jwi.item.IExceptionEntryProxy;
import edu.mit.jwi.item.IHasPartOfSpeech;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.IIndexWordID;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.IndexWordID;
import edu.mit.jwi.item.PartOfSpeech;

/**
 * Basic implementation of the <tt>IDictionary</tt> interface, intended for
 * use with the Wordnet 2.1/3.0 distribution. A path to the Wordnet 2.1/3.0
 * dictionary files must be provided. If no <tt>IDataProvider</tt> or
 * <tt>IParserProvider</tt> is specified, it uses the base implementations
 * provided with the distribution.
 * <p>
 * This dictionary caches items it retrieves. The cache is limited in its size
 * by default. See {@link edu.mit.jwi.dict.IClassCache#DEFAULT_MAXIMUM_CAPACITY}.
 * If you find this default maximum size does suit your purposes, you can change
 * it by first calling the {@link #open()} method, then by retrieving the cache
 * via the {@link #getCache()} method and setting the maximum cache size via the
 * {@link edu.mit.jwi.dict.IClassCache#setMaximumCapacity(int)} method. If you a
 * specialized implementation, you can subclass the <tt>Dictionary</tt> class
 * and override the {@link #initCache()} method.
 * 
 * @author Mark A. Finlayson
 * @version 1.1, 4/28/07
 * @since 1.5.0
 */

public class Dictionary implements IDictionary {

	URL fUrl = null;
	IDataProvider fDataProvider = null;
	IParserProvider fParserProvider = null;
	ILineParser<IIndexWord> fIndexParser = null;
	ILineParser<ISynset> fDataParser = null;
	ILineParser<IExceptionEntryProxy> fExceptionParser = null;
	IClassCacheIDAware fCache;

	private static final Class[] cacheClasses = new Class[] { IIndexWord.class, IWord.class, ISynset.class,
			IExceptionEntry.class };

	/**
	 * Constructs a default dictionary.
	 */
	public Dictionary(URL url) {
		this(url, new ParserProvider());
	}

	/**
	 * Constructs a dictionary with a caller-specified <tt>IFileProvider<tt>,
	 * but default implementation of an <tt>IParserProvider</tt>.
	 */
	public Dictionary(IDataProvider provider) {
		this(provider, new ParserProvider());
	}

	/**
	 * Constructs a dictionary with a caller-specified <tt>IParserProvider</tt>,
	 * but default implementation of an <tt>IFileProvider<tt>
	 */
	public Dictionary(URL url, IParserProvider parserProvider) {
		this((IDataProvider) null, parserProvider);
		fUrl = url;
	}

	/**
	 * Constructs a dictionary with a caller-specified <tt>IFileProvider</tt>
	 * and <tt>IParserProvider</tt>.
	 */
	public Dictionary(IDataProvider dataProvider, IParserProvider parserProvider) {
		setDataProvider(dataProvider);
		setParserProvider(parserProvider);
	}

	public void setDataProvider(IDataProvider provider) {
		fDataProvider = provider;
	}

	public void setParserProvider(IParserProvider provider) {
		fParserProvider = provider;
		if (fParserProvider != null) {
			fIndexParser = (ILineParser<IIndexWord>) provider.getParser(WordnetDataType.INDEX);
			fDataParser = (ILineParser<ISynset>) provider.getParser(WordnetDataType.DATA);
			fExceptionParser = (ILineParser<IExceptionEntryProxy>) provider.getParser(WordnetDataType.EXCEPTION);
		}
		else {
			fIndexParser = null;
			fDataParser = null;
			fExceptionParser = null;
		}
	}

	public IDataProvider getDataProvider() {
		return fDataProvider;
	}

	public IParserProvider getParserProvider() {
		return fParserProvider;
	}

	/**
	 * If no data provider has specified at the time this method is called, the
	 * Dictionary tries to construct a default data provider, a
	 * <tt>FileProvider</tt> on the
	 * 
	 * @see IDictionary#open()
	 */
	public boolean open() {
		if (fDataProvider == null) fDataProvider = new FileProvider(fUrl);
		try {
			fDataProvider.open();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		fCache = initCache();
		return true;
	}

	/**
	 * Creates the cache. Subclasses may override or extend. The cache may be
	 * null;
	 */
	protected IClassCacheIDAware initCache() {
		return new ClassCacheIDAware(cacheClasses);
	}

	/**
	 * Gets the cache, so it can be configured. Returns <code>null</code>
	 * until the dictionary has been opened.
	 */
	public IClassCacheIDAware getCache() {
		return fCache;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.wordnet.core.dict.IDictionary#close()
	 */
	public void close() {
		if (fCache != null) {
			fCache.releaseCaches(cacheClasses);
			fCache = null;
		}
		if (fDataProvider != null) fDataProvider.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.wordnet.core.dict.IDictionary#isOpen()
	 */
	public boolean isOpen() {
		if (fDataProvider == null) return false;
		return fDataProvider.isOpen();
	}

	protected void checkOpen() {
		if (!isOpen()) throw new DictionaryNotOpenException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.wordnet.core.dict.IDictionary#lookupIndexWord(java.lang.String,
	 *      edu.mit.wordnet.core.data.PartOfSpeech)
	 */
	public IIndexWord getIndexWord(String lemma, PartOfSpeech pos) {
		checkOpen();
		return getIndexWord(new IndexWordID(lemma, pos));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.wordnet.core.dict.IDictionary#getIndexWord(edu.mit.wordnet.core.data.IIndexWordID)
	 */
	public IIndexWord getIndexWord(IIndexWordID id) {
		checkOpen();
		if (id == null) return null;

		IIndexWord result = null;
		if (fCache != null) result = (IIndexWord) fCache.retrieve(id);

		if (result == null & fIndexParser != null) {
			IContentType content = WordnetContentType.getContentType(id.getPartOfSpeech(), WordnetDataType.INDEX);
			IDictionaryDataSource file = fDataProvider.getFile(content);
			String line = file.getLine(id.getLemma());
			if (line == null) return null;
			result = fIndexParser.parseLine(line);
			if (fCache != null) fCache.cache(result);
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.wordnet.core.dict.IDictionary#getWord(edu.mit.wordnet.core.data.IWordID)
	 */
	public IWord getWord(IWordID id) {
		checkOpen();
		if (id == null) return null;
		IWord result = null;
		if (fCache != null) result = (IWord) fCache.retrieve(id);

		if (result == null) {
			ISynset synset = getSynset(id.getSynsetID());
			// because of the peculiarities of the Wordnet distribution, one or
			// the other of
			// the WordID number or lemma may not exist. So we have to check
			// them before trying.
			if (id.getNumber() > 0) {
				result = synset.getWords()[id.getNumber() - 1];
			}
			else if (id.getLemma() != null) {
				for (IWord word : synset.getWords()) {
					if (word.getLemma().equalsIgnoreCase(id.getLemma())) {
						result = word;
						break;
					}
				}
			}
			else {
				throw new IllegalArgumentException("Not enough information in IWordID instance to retrieve word.");
			}

			if (fCache != null) fCache.cache(result);
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.wordnet.core.dict.IDictionary#getSynset(edu.mit.wordnet.core.data.ISynsetID)
	 */
	public ISynset getSynset(ISynsetID id) {
		checkOpen();
		if (id == null) return null;

		ISynset result = null;
		if (fCache != null) result = (ISynset) fCache.retrieve(id);

		if (result == null & fDataParser != null) {
			IContentType content = WordnetContentType.getContentType(id.getPartOfSpeech(), WordnetDataType.DATA);
			IDictionaryDataSource file = fDataProvider.getFile(content);
			String line = file.getLine(Long.toString(id.getOffset()));
			if (line == null) return null;
			result = fDataParser.parseLine(line);
			if (fCache != null) fCache.cache(result);
		}

		return result;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.wordnet.dict.IDictionary#getExceptionEntry(java.lang.String,
	 *      edu.mit.wordnet.data.PartOfSpeech)
	 */
	public IExceptionEntry getExceptionEntry(String surfaceForm, PartOfSpeech pos) {
		return getExceptionEntry(new ExceptionEntryID(surfaceForm, pos));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.wordnet.dict.IDictionary#getExceptionEntry(edu.mit.wordnet.data.IExceptionEntryID)
	 */
	public IExceptionEntry getExceptionEntry(IExceptionEntryID id) {
		checkOpen();
		if (id == null) return null;

		IExceptionEntry result = null;
		if (fCache != null) result = (IExceptionEntry) fCache.retrieve(id);

		if (result == null & fExceptionParser != null) {
			IContentType content = WordnetContentType.getContentType(id.getPartOfSpeech(), WordnetDataType.EXCEPTION);
			IDictionaryDataSource file = fDataProvider.getFile(content);
			String line = file.getLine(id.getSurfaceForm());
			if (line == null) return null;
			IExceptionEntryProxy proxy = fExceptionParser.parseLine(line);
			if (proxy != null) result = new ExceptionEntry(proxy, id.getPartOfSpeech());
			if (fCache != null) fCache.cache(result);
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.wordnet.core.dict.IDictionary#getWordIterator(edu.mit.wordnet.core.data.PartOfSpeech)
	 */
	public Iterator<IIndexWord> getIndexWordIterator(PartOfSpeech pos) {
		checkOpen();
		return new IndexFileIterator(pos);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.wordnet.core.dict.IDictionary#getWordPatternIterator(java.lang.String)
	 */
	public Iterator<IIndexWord> getIndexWordPatternIterator(PartOfSpeech pos, String pattern) {
		checkOpen();
		return new IndexFilePatternIterator(pos, pattern);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.wordnet.core.dict.IDictionary#getSynsetIterator(edu.mit.wordnet.core.data.PartOfSpeech)
	 */
	public Iterator<ISynset> getSynsetIterator(PartOfSpeech pos) {
		checkOpen();

		return new DataFileIterator(pos);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.wordnet.dict.IDictionary#getExceptionEntryIterator(edu.mit.wordnet.data.PartOfSpeech)
	 */
	public Iterator<IExceptionEntryProxy> getExceptionEntryIterator(PartOfSpeech pos) {
		if (!isOpen()) return null;
		return new ExceptionFileIterator(pos);
	}

	/**
	 * Returns the initial part of this string that <b>must</b> match, i.e.,
	 * the part of the string up to but not including the first wildcard.
	 */
	public static String getPatternRoot(String pattern, boolean ignoreWildCards) {
		if (!ignoreWildCards) {
			// strip off first wildcard and everything after
			int idxQ = pattern.indexOf('?');
			if (idxQ == -1) idxQ = pattern.length();
			int idxS = pattern.indexOf('*');
			if (idxS == -1) idxS = pattern.length();
			int idx = Math.min(idxQ, idxS);
			return pattern.substring(0, idx);
		}
		else {
			return pattern;
		}
	}

	/**
	 * Abstract class used for iterating over line-based files.
	 */
	public abstract class FileIterator<T> implements Iterator<T>, IHasPartOfSpeech {

		protected final IDictionaryDataSource fFile;
		protected final Iterator<String> iterator;
		protected String currentLine;

		public FileIterator(IContentType content) {
			this(content, null);
		}

		public FileIterator(IContentType content, String startKey) {
			this.fFile = fDataProvider.getFile(content);
			this.iterator = fFile.iterator(startKey);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see edu.mit.wordnet.data.IHasPartOfSpeech#getPartOfSpeech()
		 */
		public PartOfSpeech getPartOfSpeech() {
			return fFile.getContentType().getPartOfSpeech();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext() {
			return iterator.hasNext();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Iterator#next()
		 */
		public T next() {
			currentLine = iterator.next();
			return parseLine(currentLine);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Iterator#remove()
		 */
		public void remove() {
			iterator.remove();
		}

		/** Parses the line using a parser provided at construction time */
		public abstract T parseLine(String line);
	}

	/**
	 * Iterates over index files.
	 */
	public class IndexFileIterator extends FileIterator<IIndexWord> {

		public IndexFileIterator(PartOfSpeech pos) {
			this(pos, "");
		}

		public IndexFileIterator(PartOfSpeech pos, String pattern) {
			super(WordnetContentType.getContentType(pos, WordnetDataType.INDEX), pattern);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see edu.mit.wordnet.core.base.dict.Dictionary.FileIterator#parseLine(java.lang.String)
		 */
		public IIndexWord parseLine(String line) {
			return fIndexParser.parseLine(line);
		}

	}

	/**
	 * Iterates over index files. This is a look-ahead iterator that uses a
	 * pattern.
	 */
	public class IndexFilePatternIterator extends IndexFileIterator {

		IIndexWord previous, next;
		StringMatcher matcher = null;
		Comparator<String> fComparator;
		String fPatternRoot;

		public IndexFilePatternIterator(PartOfSpeech pos, String pattern) {
			super(pos, getPatternRoot(pattern, false));
			if (pattern == null) throw new IllegalArgumentException("Pattern cannot be null in IndexFilePatterIterator");
			matcher = new StringMatcher(pattern, true, false);
			fPatternRoot = matcher.getPatternRoot();
			fComparator = fFile.getContentType().getLineComparator();
			advance();
		}

		/**
		 * Advances to the next match that will be returned by the iterator.
		 */
		protected void advance() {
			do {
				currentLine = iterator.next();
				if (currentLine == null) {
					next = null;
					return;
				}
				next = parseLine(currentLine);
				if (next == null) break;
				if (!next.getLemma().startsWith(fPatternRoot)) {
					next = null;
					break;
				}
			} while (!matcher.match(next.getLemma()));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext() {
			return next != null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Iterator#next()
		 */
		public IIndexWord next() {
			previous = next;
			advance();
			return previous;
		}
	}

	/**
	 * Iterates over data files.
	 */
	public class DataFileIterator extends FileIterator<ISynset> {

		public DataFileIterator(PartOfSpeech pos) {
			super(WordnetContentType.getContentType(pos, WordnetDataType.DATA));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see edu.mit.wordnet.core.base.dict.Dictionary.FileIterator#parseLine(java.lang.String)
		 */
		public ISynset parseLine(String line) {
			return fDataParser.parseLine(line);
		}

	}

	/**
	 * Iterates over exception files.
	 */
	public class ExceptionFileIterator extends FileIterator<IExceptionEntryProxy> {

		public ExceptionFileIterator(PartOfSpeech pos) {
			super(WordnetContentType.getContentType(pos, WordnetDataType.EXCEPTION));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see edu.mit.wordnet.dict.Dictionary.FileIterator#parseLine(java.lang.String)
		 */
		public IExceptionEntryProxy parseLine(String line) {
			return fExceptionParser.parseLine(line);
		}
	}

	/**
	 * The following class is taken from the Eclipse API, and made available
	 * under the terms of the Eclipse Public Licence.
	 * <p>
	 * Copyright (c) 2000, 2004 IBM Corporation and others. All rights reserved.
	 * This program and the accompanying materials are made available under the
	 * terms of the Eclipse Public License v1.0 which accompanies this
	 * distribution, and is available at
	 * http://www.eclipse.org/legal/epl-v10.html Contributors: IBM Corporation -
	 * initial API and implementation
	 */

}
