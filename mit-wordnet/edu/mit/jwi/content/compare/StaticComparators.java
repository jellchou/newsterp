/******************************************************************************
 * Copyright (c) 2007 Mark Alan Finlayson
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT Java Wordnet Interface 
 * Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.mit.edu/~markaf/projects/wordnet/license.html.
 *****************************************************************************/

package edu.mit.jwi.content.compare;

import java.util.Comparator;

/**
 * Provides several static comparators that are used in the default Wordnet
 * content types. See {@link edu.mit.jwi.content.WordnetContentType}.
 * This class is merely a repository of a few static fields, with getters
 * that dynamically construct the arguments, and so it is neither intended
 * to be subclasses nor instantiated.
 * 
 * @author Mark A. Finlayson
 * @version 1.00, 04/15/07
 * @since 1.5.0
 */
public final class StaticComparators {

    private static CommentComparator commentComparator;
    private static Comparator<String> indexComparator;
    private static Comparator<String> dataComparator;
    private static Comparator<String> exceptionComparator;

    /**
     * This class is not intended to be instantiated.
     */
    private StaticComparators() {
    }

    /**
     * @return the static comment comparator
     */
    public static CommentComparator getCommentComparator() {
        if (commentComparator == null)
            commentComparator = new CommentComparator();
        return commentComparator;
    }

    /**
     * @return the static index line comparator
     */
    public static Comparator<String> getIndexLineComparator() {
        if (indexComparator == null)
            indexComparator = new IndexLineComparator(getCommentComparator());
        return indexComparator;
    }

    /**
     * @return the static data line comparator
     */
    public static Comparator<String> getDataLineComparator() {
        if (dataComparator == null)
            dataComparator = new DataLineComparator(getCommentComparator());
        return dataComparator;
    }

    /**
     * @return the static exception line comparator
     */
    public static Comparator<String> getExceptionComparator() {
        if (exceptionComparator == null)
            exceptionComparator = new ExceptionLineComparator();
        return exceptionComparator;
    }

}
