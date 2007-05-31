/*
 * NewsTerp Engine - We report.  You decipher.
 * copyright (c) 2007 Colin Bayer, Jack Hebert
 *
 * CSE 472 Spring 2007 final project
 */

import edu.mit.jwi.item.IVerbFrame;

public class Predicate {
	public Predicate(TaggedWord[] aWords) {
		mWords = aWords;
	}

	public String toString() {
		if (mWords == null) return "(null)";

		String rv = "";

		for (TaggedWord w : mWords) {
			rv += " " + w;
		}

		return rv.substring(1);
	}

	public String toSerialRep() {
		if (mWords == null) return "(null)";

		String rv = "";

		for (TaggedWord w : mWords) {
			rv += " " + w.getWord();
		}

		return rv.substring(1);
	}

	public IVerbFrame[] getSubcatFrames() {
		return null;
	}

	private TaggedWord[] mWords;
}
