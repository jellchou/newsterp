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
 * Default implementation of <tt>IIndexWordID</tt>
 * 
 * @author Mark A. Finlayson
 * @version 1.00, 4/14/06
 * @since 1.5.0
 */
public class IndexWordID implements IIndexWordID {

    final String fLemma;
    final PartOfSpeech fPOS;

    public IndexWordID(String lemma, PartOfSpeech pos) {
        fLemma = lemma;
        fPOS = pos;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.data.IIndexWordID#getLemma()
     */
    public String getLemma() {
        return fLemma;
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
    public Class<IIndexWord> getIdentifiedClass() {
        return IIndexWord.class;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((fLemma == null) ? 0 : fLemma.hashCode());
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
        final IndexWordID other = (IndexWordID) obj;
        if (fLemma == null) {
            if (other.fLemma != null) return false;
        } else if (!fLemma.equals(other.fLemma)) return false;
        if (fPOS == null) {
            if (other.fPOS != null) return false;
        } else if (!fPOS.equals(other.fPOS)) return false;
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "XID-" + fLemma + "-" + fPOS.getTag();
    }

    /**
     * Convenience method for transforming the result of the {@link #toString()}
     * method into an <tt>IndexWordID</tt>
     * 
     * @return IndexWordID The parsed id, or null if the string is malformed
     */
    public static IndexWordID parseIndexWordID(String value) {
        if (value == null) return null;
        if (!value.startsWith("XID-")) return null;
        int begin, end;

        // Get lemma
        begin = 4;
        end = value.lastIndexOf('-');
        if (end < begin) return null;
        String lemma = value.substring(begin, end);
        if (lemma.length() == 0) return null;

        // get POS
        begin = end + 1;
        if (begin >= value.length()) return null;
        char tag = Character.toLowerCase(value.charAt(begin));
        PartOfSpeech pos = null;
        try {
            pos = PartOfSpeech.getPartOfSpeech(tag);
        } catch (RuntimeException e) {
            return null;
        }
        if (pos == null) return null;

        return new IndexWordID(lemma, pos);
    }

}
