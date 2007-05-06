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
 * Objects that implement this interface act as both detectors for comment lines
 * in data resources, and comparators that say how comment lines are ordered, if
 * at all.
 * 
 * @author Mark A. Finlayson
 * @version 1.00, 04/15/06
 * @since 1.5.0
 */
public interface ICommentDetector extends Comparator<String> {

	/**
	 * @return <code>true</code> if the specified string is a comment line,
	 *         <code>false</code> otherwise.
	 */
	public boolean isCommentLine(String line);
}
