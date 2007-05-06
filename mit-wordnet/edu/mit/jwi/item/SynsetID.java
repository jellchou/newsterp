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
 * Default implementation of <tt>ISynsetID</tt>
 * 
 * @author Mark A. Finlayson
 * @version 1.00, 4/13/06
 * @since 1.5.0
 */
public class SynsetID implements ISynsetID {

    private long fOffset = -1;
    private PartOfSpeech fPOS = null;

    public SynsetID(long offset, PartOfSpeech pos) {
        if (pos == null)
            throw new IllegalArgumentException(
                    "Part of speech cannot be null for SynsetID constructor");
        fOffset = offset;
        fPOS = pos;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.data.ISynsetID#getOffset()
     */
    public long getOffset() {
        return fOffset;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.data.ISynsetID#getPartOfSpeech()
     */
    public PartOfSpeech getPartOfSpeech() {
        return fPOS;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.item.IItemID#getIdentifiedClass()
     */
    public Class<ISynset> getIdentifiedClass() {
        return ISynset.class;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + (int) (fOffset ^ (fOffset >>> 32));
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
        final SynsetID other = (SynsetID) obj;
        if (fOffset != other.fOffset) return false;
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
        return "SID-" + fOffset + "-" + fPOS.getTag();
    }

    /**
     * Convenience method for transforming the result of the {@link #toString()}
     * method back into an <tt>ISynsetID</tt>
     * 
     * @return SynsetID The parsed id, or null if the string is malformed
     */
    public static SynsetID parseSynsetID(String value) {
        if (value == null) return null;
        if (!value.startsWith("SID-")) return null;
        int begin, end;
        begin = 4;
        end = value.indexOf('-', begin);
        if (end < begin) return null;
        long offset = 0;
        try {
            offset = Long.parseLong(value.substring(begin, end));
        } catch (NumberFormatException e) {
            return null;
        }
        begin = end + 1;
        if (begin >= value.length()) return null;
        char tag = Character.toLowerCase(value.charAt(begin));
        PartOfSpeech pos = null;
        try {
            pos = PartOfSpeech.getPartOfSpeech(tag);
        } catch (RuntimeException e) {
            return null;
        }

        return new SynsetID(offset, pos);
    }
}
