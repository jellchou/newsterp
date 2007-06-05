/*
 * NewsTerp Engine - We report.  You decipher.
 * copyright (c) 2007 Colin Bayer, Jack Hebert
 *
 * CSE 472 Spring 2007 final project
 */

public class SentenceNoAnnotation implements Annotation {
	public SentenceNoAnnotation(int aNumber) {
		mNumber = aNumber;
	}

	public String getType() { return "snum"; }

	public String toString() {
		return getType() + ":" + mNumber;
	}

	public String toSerialRep() { return toString(); }

	private int mNumber;
}
