/******************************************************************************
 * Copyright (c) 2007 Mark Alan Finlayson
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT Java Wordnet Interface 
 * Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.mit.edu/~markaf/projects/wordnet/license.html.
 *****************************************************************************/

package edu.mit.jwi.data;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.mit.jwi.content.IContentType;
import edu.mit.jwi.content.WordnetContentType;
import edu.mit.jwi.content.compare.StaticComparators;

/**
 * A basic implementation of the <tt>IDataProvider</tt> interface that uses
 * files in the file system to back instances of <tt>IDictionaryDataSource</tt>.
 * It takes a url to a filesystem directory as its path argument, and uses the
 * hints from the <tt>getResourceNameHints()</tt> methods on the
 * <tt>IPartOfSpeech<tt> and <tt>IDictionaryDataSourceType</tt>
 * interfaces to examine the filenames in the that directory to determine which 
 * files contain which data.
 * <p>
 * The 
 * <p>
 * This version has been confirmed to work with both Wordnet versions 2.1 and 3.0,
 * in both Windows and UNIX versions.
 * 
 * @author  Mark A. Finlayson
 * @version 1.1, 04/28/07
 * @since   1.5.0
 */
public class FileProvider implements IDataProvider {

	public static final String PROTOCOL_FILE = "file";
	private URL fUrl = null;
	private IContentType[] fContentTypes = null;

	private Map<IContentType, IDictionaryDataSource> fileMap = null;

	/**
	 * Constructs the file provider pointing to the resource indicated by the
	 * path.
	 * 
	 * @param url
	 *            A file URL in UTF-8 decodable format
	 */
	public FileProvider(URL url) {
		this(url, WordnetContentType.values());
	}

	/**
	 * Allows the instantiator to specify which content types this file provider
	 * will look for when the <tt>openFile()</tt> method is called.
	 * 
	 * @param url
	 *            A file URL in UTF-8 decodable format
	 */
	public FileProvider(URL url, IContentType[] contentTypes) {
		setSource(url);
		fContentTypes = contentTypes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.data.IDataProvider#setSource(java.net.URL)
	 */
	public void setSource(URL url) {
		fUrl = url;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.data.IDataProvider#getSource()
	 */
	public URL getSource() {
		return fUrl;
	}

	/**
	 * @see edu.mit.jwi.data.IDataProvider#open()
	 * @throws IOException
	 *             if the dictionary directory does not exist or the directory
	 *             is empty, or there is a problem with a file
	 */
	public void open() throws IOException {
		File directory = getDirectoryHandle();
		if (!directory.exists()) throw new IOException("Dictionary directory does not exist: " + directory);

		List<File> files = new ArrayList<File>();
		File[] filesInDir = directory.listFiles(new FileFilter(){

			public boolean accept(File file) {
				return file.isFile();
			}
		});

		if (filesInDir.length == 0) throw new IOException("No files found in " + directory);
		files.addAll(Arrays.asList(filesInDir));
		fileMap = new HashMap<IContentType, IDictionaryDataSource>();

		File file;
		String name;
		IDictionaryDataType fileType;
		String[] typePatterns, posPatterns;
		for (IContentType type : fContentTypes) {
			fileType = type.getDataType();
			typePatterns = fileType.getResourceNameHints();
			if (type.getPartOfSpeech() != null) {
				posPatterns = type.getPartOfSpeech().getResourceNameHints();
			}
			else {
				posPatterns = new String[] {};
			}

			for (Iterator<File> i = files.iterator(); i.hasNext();) {
				file = i.next();
				name = file.getName();
				if (containsOneOf(name, typePatterns) & containsOneOf(name, posPatterns)) {
					i.remove();
					registerFile(new WordnetFile(file, type, StaticComparators.getCommentComparator()), type);
					break;
				}
			}
		}
		return;
	}

	protected void checkOpen() {
		if (!isOpen()) throw new DataProviderNotOpenException();
	}

	/**
	 * Translates the source URL into a java <tt>File</tt> object for access
	 * to the local filesystem. The URL must be in a UTF-8 compatible format as
	 * specified in {@link java.net.URLDecoder}
	 * 
	 * @return A <tt>File</tt> object pointing to the Wordnet dictionary data
	 *         directory.
	 * @throws NullPointerException
	 *             if url is <code>null</code>
	 * @throws IOException
	 *             if url does not use the 'file' protocol
	 */
	public File getDirectoryHandle() throws IOException {
		if (!fUrl.getProtocol().equals(PROTOCOL_FILE)) throw new IOException("URL source must use 'file' protocol");
		if (fUrl == null) throw new NullPointerException("Source not set (url=null)");
		try {
			return new File(URLDecoder.decode(fUrl.getPath(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Checks to see if one of the string patterns specified is found in the
	 * string. If so, returns <code>true</code>. It will also return
	 * <code>true</code> if the pattern array is non-null, but is of zero
	 * length. If neither of these conditions holds, it returns
	 * <code>false</code>.
	 */
	protected boolean containsOneOf(String string, String[] patterns) {
		if (patterns == null) return false;
		if (patterns.length == 0) return true;
		for (String pattern : patterns) {
			if (string.indexOf(pattern) > -1) return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.data.IDataProvider#close()
	 */
	public void close() {
		fileMap = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.data.IDataProvider#isOpen()
	 */
	public boolean isOpen() {
		return fileMap != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.data.IDataProvider#getFile(edu.mit.jwi.content.IContentType)
	 */
	public IDictionaryDataSource getFile(IContentType type) {
		checkOpen();
		IDictionaryDataSource file = fileMap.get(type);
		if (file == null) throw new RuntimeException("No file of type " + type.toString() + " found by file provider.");
		return file;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.data.IDataProvider#registerFile(edu.mit.jwi.data.IDictionaryDataSource,
	 *      edu.mit.jwi.content.IContentType)
	 */
	public void registerFile(IDictionaryDataSource file, IContentType type) {
		checkOpen();
		fileMap.put(type, file);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.data.IDataProvider#getAllFiles()
	 */
	public IDictionaryDataSource[] getAllFiles() {
		checkOpen();
		return fileMap.values().toArray(new IDictionaryDataSource[fileMap.values().size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.data.IDataProvider#iterator()
	 */
	public Iterator<IDictionaryDataSource> iterator() {
		checkOpen();
		return fileMap.values().iterator();
	}
}
