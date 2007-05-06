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
 * A comparator that captures the ordering of lines in Wordnet 2.1 data files
 * (data.adv or adv.dat files, for example). These files are ordered by offset.
 * 
 * @author Mark A. Finlayson
 * @version 1.00, 04/15/06
 * @since 1.5.0
 */
public class DataLineComparator implements Comparator<String> {

	private CommentComparator fDetector = null;

	public DataLineComparator(CommentComparator detector) {
		fDetector = detector;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(T, T)
	 */
	public int compare(String s1, String s2) {

		boolean c1 = fDetector.isCommentLine(s1), c2 = fDetector
				.isCommentLine(s2);

		if (c1 & c2) {
			// both lines are comments, defer to comment comparator
			return fDetector.compare(s1, s2);
		} else if (c1 & !c2) {
			// first line is a comment, should come before the other
			return -1;
		} else if (!c1 & c2) {
			// second line is a comment, should come before the other
			return 1;
		}

		// Neither strings are comments, so extract the offset from the
		// beginnings of both
		// and compare them as two longs.

		int i1 = s1.indexOf(' '), i2 = s2.indexOf(' ');
		if (i1 == -1)
			i1 = s1.length();
		if (i2 == -1)
			i2 = s2.length();

		String sub1 = s1.substring(0, i1), sub2 = s2.substring(0, i2);

		long l1 = Long.parseLong(sub1), l2 = Long.parseLong(sub2);
		if (l1 < l2) {
			return -1;
		} else if (l1 > l2) {
			return 1;
		}
		return 0;
	}
}
