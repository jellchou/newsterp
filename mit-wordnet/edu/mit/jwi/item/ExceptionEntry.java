/******************************************************************************
 * Copyright (c) 2007 Mark Alan Finlayson
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT Java Wordnet Interface 
 * Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.mit.edu/~markaf/projects/wordnet/license.html.
 *****************************************************************************/

package edu.mit.jwi.item;

/**
 * Default implementation of an <tt>IExceptionEntry</tt>.
 * 
 * @author Mark A. Finlayson
 * @version 1.00, Apr 20, 2007
 * @since 1.5.0
 */
public class ExceptionEntry extends ExceptionEntryProxy implements
        IExceptionEntry {

    final PartOfSpeech fPos;
    final IExceptionEntryID fID;

    /**
     * The part of speech must not be null; if it is, the constructor throws an
     * <tt>IllegalArgumentException</tt>.
     */
    public ExceptionEntry(IExceptionEntryProxy proxy, PartOfSpeech pos) {
        super(proxy);
        if (pos == null)
            throw new IllegalArgumentException(
                    "Initializing values cannot be null");
        fPos = pos;
        fID = new ExceptionEntryID(getSurfaceForm(), fPos);
    }

    /**
     * The part of speech must not be null; if it is, the constructor throws an
     * <tt>IllegalArgumentException</tt>.
     */
    public ExceptionEntry(String surfaceForm, String[] rootForms,
            PartOfSpeech pos) {
        super(surfaceForm, rootForms);
        if (pos == null)
            throw new IllegalArgumentException(
                    "Initializing values cannot be null");
        fPos = pos;
        fID = new ExceptionEntryID(getSurfaceForm(), fPos);
    }

    /**
     * The part of speech must not be null; if it is, the constructor throws an
     * <tt>IllegalArgumentException</tt>.
     */
    public ExceptionEntry(String[] forms, PartOfSpeech pos) {
        super(forms);
        if (pos == null)
            throw new IllegalArgumentException(
                    "Initializing values cannot be null");
        fPos = pos;
        fID = new ExceptionEntryID(getSurfaceForm(), fPos);
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.data.IHasPartOfSpeech#getPartOfSpeech()
     */
    public PartOfSpeech getPartOfSpeech() {
        return fPos;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.item.IHasID#getID()
     */
    public IExceptionEntryID getID() {
        return fID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return super.toString() + "-" + fPos.toString();
    }

}
