/*
 * NewsTerp Engine - We report.  You decipher.
 * copyright (c) 2007 Colin Bayer, Jack Hebert
 *
 * CSE 472 Spring 2007 final project
 */

import java.util.ArrayList;

public class TaggedArticle {
	public TaggedArticle(String aID) {
		mID = aID;
		mSentences = new ArrayList<TaggedSentence>();
	}

	public String getID() { return mID; }

	public void append(TaggedSentence aSent) {
		mSentences.add(aSent);
	}

	public TaggedSentence[] getSentences() { 
		return mSentences.toArray(new TaggedSentence[0]);
	}

	public String toString() {
		String rv = "Article `" + mID + "' (" + mSentences.size() + 
			" sentences):\n";

		for (TaggedSentence sent : mSentences) {
			rv += sent.toString() + "\n";
		}

		return rv;
	}

	private String mID;

	private ArrayList<TaggedSentence> mSentences;
}
