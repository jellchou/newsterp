/*
 * NewsTerp Engine - We report.  You decipher.
 * copyright (c) 2007 Colin Bayer, Jack Hebert
 *
 * CSE 472 Spring 2007 final project
 */

public class HumanReadableSentenceAnnotation implements Annotation {
	public HumanReadableSentenceAnnotation(String aSentence) {
		mSentence = aSentence;
	}

	public String getType() { return "hrs"; }

	public String toString() {
		return getType() + ":" + mSentence;
	}

	public String toSerialRep() { return toString(); }

	private String mSentence;
}
