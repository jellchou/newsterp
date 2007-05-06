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

import edu.mit.jwi.content.MisformattedLineException;

/**
 * A comparator that captures the ordering of lines in Wordnet 2.1 exception
 * files (exc.adv or adv.exc files, for example). These files are ordered
 * alphabetically.
 * 
 * @author Mark A. Finlayson
 * @version 1.00, 04/15/07
 * @since 1.5.0
 */
public class ExceptionLineComparator implements Comparator<String> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(T, T)
	 */
	public int compare(String line1, String line2) {

		String[] words1 = line1.split(" ");
		String[] words2 = line2.split(" ");

		if (words1.length < 1)
			throw new MisformattedLineException(line1);
		if (words2.length < 1)
			throw new MisformattedLineException(line2);

		return words1[0].compareTo(words2[0]);
	}

}
