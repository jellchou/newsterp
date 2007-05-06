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
 * Basic implementation of a comment detector that is designed for comments
 * found at the head of Wordnet 2.1 & 3.0 dictionary files. It assumes that each
 * comment line starts with two spaces, followed by a number that indicates the
 * position of the comment line relative to the rest of the comment lines in the
 * file.
 * 
 * @author Mark A. Finlayson
 * @version 1.00, 04/15/06
 * @since 1.5.0
 */
public class CommentComparator implements Comparator<String>, ICommentDetector {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(T, T)
	 */
	public int compare(String s1, String s2) {
		s1 = s1.trim();
		s2 = s2.trim();
		int idx1 = s1.indexOf(' '), idx2 = s2.indexOf(' ');
		if (idx1 == -1)
			idx1 = s1.length();
		if (idx2 == -1)
			idx2 = s2.length();
		String sub1 = s1.substring(0, idx1), sub2 = s2.substring(0, idx2);
		int num1 = Integer.parseInt(sub1), num2 = Integer.parseInt(sub2);
		if (num1 < num2) {
			return -11;
		} else if (num1 > num2) {
			return 1;
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.wordnet.core.content.ICommentDetector#isCommentLine(java.lang.String)
	 */
	public boolean isCommentLine(String line) {
		if (line == null)
			return false;
		if (line.length() < 2)
			return false;
		if (line.charAt(0) == ' ' & line.charAt(1) == ' ')
			return true;
		return false;
	}
}
