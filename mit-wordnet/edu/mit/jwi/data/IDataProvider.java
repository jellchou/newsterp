/******************************************************************************
 * Copyright (c) 2007 Mark Alan Finlayson
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT Java Wordnet Interface 
 * Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.mit.edu/~markaf/projects/wordnet/license.html.
 *****************************************************************************/

package edu.mit.jwi.data;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import edu.mit.jwi.content.IContentType;

/**
 * Objects that implement this interface serve as managers of the relationship
 * of a {@link edu.mit.jwi.dict.IDictionary} object and the dictionary data.
 * Before using an <tt>IFileProvider</tt>, the dictionary must call
 * {@link #setSource(URL)} and {@link #open()}. The first call is to
 * direct the file provider to the location of the data files. The second
 * instructs the file provider to do any intialization necessary so that it is
 * ready to return the handle to the data. External classes may add their own
 * data files, registered to a particular content type, by using the
 * {@link #registerFile(IDictionaryDataSource, IContentType)} method. When the dictionary wants to lookup a piece of data,
 * be it an {@link edu.mit.jwi.item.IWord},
 * {@link edu.mit.jwi.item.ISynset},
 * {@link edu.mit.jwi.item.IIndexWord}, or other object, it requests the
 * <tt>IDictionaryDataSource</tt> object that holds that data by passing the
 * appropriate {@link edu.mit.jwi.content.IContentType} to the
 * {@link #getFile(IContentType)} method. That interface then handles the actual production
 * of the data from whatever resource backs it.
 * 
 * @author Mark A. Finlayson
 * @version 1.00, 04/15/06
 * @since 1.5.0
 */
public interface IDataProvider {

    /**
     * The dictionary (or user) that is using the file provider must call this
     * method with the appropriate url that directs the file provider to the
     * data that backs the dictionary. If the data provider has already been
     * opened, this call will have no effect until the provider is closed then
     * opended again.
     */
    public void setSource(URL url);

    /** Returns the url that points to the resource location */
    public URL getSource();

    /**
	 * Instructs the file provider to perform any initialization, such as
	 * creating IDictionaryDataSource objects and registering them with the
	 * appropriate IContentType.
	 * 
	 * @throws IOException
	 *             if there is a problem during initialization
	 */
    public void open() throws IOException;

    /**
     * Instructs the provider to dispose of handles to current
     * IDictionaryDataSource objects and perform any other operations necessary
     * to free the data resources that back the dictionary. A subsequent call to
     * </tt>openFiles()<tt> should put the provider in a state where it can
     * again provide file handles.
     */
    public void close();

    /**
     * Returns <code>true</code> if the provider has finished all of its
     * initialization activities and is ready to return IDictionaryDataSources
     * on command; returns <code>false</code> otherwise.
     */
    public boolean isOpen();

    /**
     * Returns an array that contains all <tt>IDictionaryDataSource</tt>
     * objects to which the file provider has access. Modifying the returned
     * array should not change the handles of the file provider; modifying the
     * IDictionaryDataSource objects, however, will change the state of those
     * objects used by the file provider.
     */
    public IDictionaryDataSource[] getAllFiles();

    /**
     * Gets an IDictionaryDataSource object that is registered to the specified
     * content type. If there is more than one <tt>IDictionaryDataSource</tt>
     * registered to the content type, the file provider must decide, on the
     * basis of some other information (unspecified in this interface), which
     * file to return.
     */
    public IDictionaryDataSource getFile(IContentType type);

    /**
     * Registers the specified file to the specified content type. In many
     * implementations this will replace the currently registered file, but need
     * not necessarily be the case.
     */
    public void registerFile(IDictionaryDataSource file, IContentType type);

    /**
     * Returns an iterator that will iterator, in no particular order, over the
     * <tt>IDictionaryDataSources</tt> to which the file provider has access.
     */
    public Iterator<IDictionaryDataSource> iterator();
}
