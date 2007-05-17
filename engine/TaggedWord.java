/*
 * NewsTerp Engine - We report.  You decipher.
 * copyright (c) 2007 Colin Bayer, Jack Hebert
 *
 * CSE 472 Spring 2007 final project
 */

public class TaggedWord {
	TaggedWord(String aWord, PartOfSpeech aPOS) {
		mWord = aWord; mPOS = aPOS;
	}

	public String getWord() { return mWord; }
	public PartOfSpeech getPOS() { return mPOS; }

	public String toString() { return mWord + "/" + mPOS; }

	public boolean equals(Object aObj) {
		if (!(aObj instanceof TaggedWord)) return false;

		TaggedWord w = (TaggedWord)aObj;
		return (w.mPOS.equals(mPOS)) && (w.mWord.equals(mWord));
	}
	public int hashCode() {
		return mWord.hashCode() ^ mPOS.hashCode();
	}

	private String mWord;
	private PartOfSpeech mPOS;
}
