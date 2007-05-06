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
 * Default implementation of <tt>IExceptionEntryProxy</tt>
 * 
 * @author Mark A. Finlayson
 * @version 1.00, 4/17/07
 * @since 1.5.0
 */
public class ExceptionEntryProxy implements IExceptionEntryProxy {

    final String fSurface;
    final String[] fRoots;

    /**
     * The argument to the constructor cannot be null, otherwise it will throw
     * an IllegalArgumentException
     */
    public ExceptionEntryProxy(IExceptionEntryProxy proxy) {
        if (proxy == null)
            throw new IllegalArgumentException(
                    "Initializing values cannot be null");
        fSurface = proxy.getSurfaceForm();
        fRoots = new String[proxy.getRootForms().length];
        System.arraycopy(proxy.getRootForms(), 0, fRoots, 0, proxy
                .getRootForms().length);
    }

    /**
     * The array and its contents cannot be null, otherwise it will throw an
     * IllegalArgumentException
     */
    public ExceptionEntryProxy(String[] forms) {
        if (forms == null)
            throw new IllegalArgumentException(
                    "Initializing values cannot be null");
        if (forms.length < 2)
            throw new IllegalArgumentException(
                    "Initializing array must have length of at least two");
        for (String form : forms) {
            if (form == null)
                throw new IllegalArgumentException(
                        "Initializing String values cannot be null");
            if (form.length() == 0)
                throw new IllegalArgumentException(
                        "Initializing String values cannot be empty");
        }
        fSurface = forms[0];
        fRoots = new String[forms.length - 1];
        System.arraycopy(forms, 1, fRoots, 0, forms.length - 1);
    }

    /**
     * The arguments, and the contents of the array, cannot be null, otherwise
     * it will throw an IllegalArgumentException
     */
    public ExceptionEntryProxy(String surfaceForm, String[] rootForms) {
        if (surfaceForm == null | rootForms == null)
            throw new IllegalArgumentException(
                    "Initializing values cannot be null");
        if (rootForms.length < 1)
            throw new IllegalArgumentException(
                    "Initializing array must have length of at least two");
        for (String form : rootForms) {
            if (form == null)
                throw new IllegalArgumentException(
                        "Initializing String values cannot be null");
            if (form.length() == 0)
                throw new IllegalArgumentException(
                        "Initializing String values cannot be empty");
        }
        fSurface = surfaceForm;
        fRoots = new String[rootForms.length];
        System.arraycopy(rootForms, 0, fRoots, 0, rootForms.length);
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.data.IExceptionEntry#getSurfaceForm()
     */
    public String getSurfaceForm() {
        return fSurface;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.data.IExceptionEntry#getRootForms()
     */
    public String[] getRootForms() {
        String[] result = new String[fRoots.length];
        System.arraycopy(fRoots, 0, result, 0, fRoots.length);
        return result;
    }

    private static String prefix = "EXC-";
    private static String rightBracket = "[";
    private static String leftBracket = "]";
    private static String comma = ", ";

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(prefix);
        sb.append(fSurface);
        sb.append(rightBracket);
        for (int i = 0; i < fRoots.length; i++) {
            sb.append(fRoots[i]);
            if (i < fRoots.length - 1) sb.append(comma);
        }
        sb.append(leftBracket);
        return sb.toString();
    }

}
