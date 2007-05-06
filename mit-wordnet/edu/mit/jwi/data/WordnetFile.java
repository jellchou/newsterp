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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Comparator;
import java.util.Iterator;

import edu.mit.jwi.content.IContentType;
import edu.mit.jwi.content.compare.ICommentDetector;

/**
 * Basic implementation of the <tt>IDictionaryDataSource</tt> interface,
 * intended for use with the Wordnet 2.1 distribution.
 * 
 * @author Mark A. Finlayson
 * @version 1.00, 04/15/06
 * @since 1.5.0
 */
public class WordnetFile extends AbstractCachingDataSource {

    private String fName = null;
    private IContentType fContentType = null;
    private Comparator<String> fComparator = null;
    private File fOriginalFile = null;
    private RandomAccessFile fRandomFile = null;
    private ICommentDetector fDetector;

    public WordnetFile(File file, IContentType contentType,
            ICommentDetector detector) throws FileNotFoundException {
        fOriginalFile = file;
        fName = file.getName();
        fContentType = contentType;
        fRandomFile = new RandomAccessFile(fOriginalFile, "r");
        fComparator = contentType.getLineComparator();
        setCommentDetector(detector);

    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.file.IDictionaryFile#setCommentDetector(edu.mit.wordnet.core.content.ICommentDetector)
     */
    public void setCommentDetector(ICommentDetector detector) {
        fDetector = detector;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.file.IDictionaryFile#getCommentDetector()
     */
    public ICommentDetector getCommentDetector() {
        return fDetector;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.file.IDictionaryFile#getLine(java.lang.String)
     */
    public String getLine(String key) {
        try {
            synchronized (fRandomFile) {
                long start = 0;
                long stop = fRandomFile.length();
                long midpoint;
                int compare;
                while (start + 1 < stop) {
                    String line;
                    midpoint = (start + stop) / 2;
                    fRandomFile.seek(midpoint);
                    line = fRandomFile.readLine();
                    if (midpoint > 0) line = fRandomFile.readLine();
                    // maybe need to replace previous line with
                    // if(midpoint > 0) line = fRandomFile.readLine();?
                    // and change while loop to while(start < stop)
                    compare = fComparator.compare(line, key);
                    if (compare == 0) {
                        return line;
                    } else if (compare > 0) {
                        stop = midpoint;
                    } else {
                        start = midpoint;
                    }
                }
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.file.IWordnetFile#getContentType()
     */
    public IContentType getContentType() {
        return fContentType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.file.IDictionaryFile#getName()
     */
    public String getName() {
        return fName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.file.IDictionaryFile#iterator()
     */
    public Iterator<String> iterator() {
        return new LineIterator(fOriginalFile);
    }

    public Iterator<String> iterator(String key) {
        return new LineIterator(fOriginalFile, key);
    }

    /**
     * Used to iterate over lines in a file. It is a look-ahead iterator.
     */
    public class LineIterator implements Iterator<String> {

        RandomAccessFile fOwnRandomFile;
        String previous, next;

        public LineIterator(File file) {
            this(file, null);
        }

        public LineIterator(File file, String key) {
            try {
                fOwnRandomFile = new RandomAccessFile(file, "r");
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            if (key == null) {
                advance();
                return;
            }

            key = key.trim();

            if (key.length() == 0) {
                advance();
            } else {
                findFirstLine(key);
            }

        }

        protected void findFirstLine(String key) {
            try {
                synchronized (fOwnRandomFile) {
                    long lastOffset = -1;
                    long start = 0;
                    long stop = fOwnRandomFile.length();
                    long offset, midpoint = -1;
                    int compare;
                    while (start + 1 < stop) {
                        String line;
                        midpoint = (start + stop) / 2;
                        fOwnRandomFile.seek(midpoint);
                        line = fOwnRandomFile.readLine();
                        offset = fOwnRandomFile.getFilePointer();
                        line = fOwnRandomFile.readLine();
                        compare = fComparator.compare(line, key);
                        // if the key matches exactly, we know we have found
                        // the start of this pattern in the file
                        if (compare == 0) {
                            next = line;
                            return;
                        } else if (compare > 0) {
                            stop = midpoint;
                        } else {
                            start = midpoint;
                        }
                        // if the key starts a line, remember it, because
                        // it may be the first occurence
                        if (line.startsWith(key)) {
                            lastOffset = offset;
                        }
                    }

                    // Getting here means that we didn't find an exact match
                    // to the key, so we take the last line that started
                    // with the pattern
                    if (lastOffset > -1) {
                        fOwnRandomFile.seek(lastOffset);
                        next = fOwnRandomFile.readLine();
                        return;
                    }

                    // If we didn't have any lines that matched the pattern
                    // then just advance to the first non-comment
                    advance();
                }
            } catch (IOException e) {
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext() {
            return next != null;
        }

        /**
         * Skips over comment lines to find the next line that would be returned
         * by the iterator in a call to next()
         */
        protected void advance() {
            next = null;
            String line;
            do {
                try {
                    line = fOwnRandomFile.readLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } while (fDetector.isCommentLine(line));
            next = line;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.Iterator#next()
         */
        public String next() {
            previous = next;
            advance();
            return previous;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.Iterator#remove()
         */
        public void remove() {
            // Ignore
        }
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
                + ((fContentType == null) ? 0 : fContentType.hashCode());
        result = PRIME * result
                + ((fDetector == null) ? 0 : fDetector.hashCode());
        result = PRIME * result
                + ((fOriginalFile == null) ? 0 : fOriginalFile.hashCode());
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
        final WordnetFile other = (WordnetFile) obj;
        if (fContentType == null) {
            if (other.fContentType != null) return false;
        } else if (!fContentType.equals(other.fContentType)) return false;
        if (fDetector == null) {
            if (other.fDetector != null) return false;
        } else if (!fDetector.equals(other.fDetector)) return false;
        if (fOriginalFile == null) {
            if (other.fOriginalFile != null) return false;
        } else if (!fOriginalFile.equals(other.fOriginalFile)) return false;
        return true;
    }
}
