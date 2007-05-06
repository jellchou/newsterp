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
 * Default implementation of <tt>IExceptionEntryID</tt>
 * 
 * @author Mark A. Finlayson
 * @version 1.00, 4/13/06
 * @since 1.5.0
 */
public class ExceptionEntryID implements IExceptionEntryID {

    final String fSurface;
    final PartOfSpeech fPOS;

    public ExceptionEntryID(String lemma, PartOfSpeech pos) {
        fSurface = lemma;
        fPOS = pos;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.data.IIndexWordID#getLemma()
     */
    public String getSurfaceForm() {
        return fSurface;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.data.IIndexWordID#getPartOfSpeech()
     */
    public PartOfSpeech getPartOfSpeech() {
        return fPOS;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.item.IItemID#getIdentifiedClass()
     */
    public Class<IExceptionEntry> getIdentifiedClass() {
        return IExceptionEntry.class;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "EID-" + fSurface + "-" + fPOS.getTag();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result
                + ((fSurface == null) ? 0 : fSurface.hashCode());
        result = PRIME * result + ((fPOS == null) ? 0 : fPOS.hashCode());
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
        final ExceptionEntryID other = (ExceptionEntryID) obj;
        if (fSurface == null) {
            if (other.fSurface != null) return false;
        } else if (!fSurface.equals(other.fSurface)) return false;
        if (fPOS == null) {
            if (other.fPOS != null) return false;
        } else if (!fPOS.equals(other.fPOS)) return false;
        return true;
    }



}
