/*
 * NewsTerp Engine - We report.  You decipher.
 * copyright (c) 2007 Colin Bayer, Jack Hebert
 *
 * CSE 472 Spring 2007 final project
 */

public class BaseEntity implements Entity {
	public BaseEntity(TaggedWord[] aWords) {
		mWords = aWords;
	}

	public boolean equals(Object aOther) {
		if (!(aOther instanceof BaseEntity)) return false;

		BaseEntity other = (BaseEntity)aOther;

		if (mWords.length != other.mWords.length) return false;

		for (int i = 0; i < mWords.length; i++) {
			if (!mWords[i].equals(other.mWords[i])) return false;
		}

		return true;
	}

	public String toString() {
		if (mWords == null || mWords.length == 0) return "(null)";

		String rv = "";

		for (TaggedWord w : mWords) {
			rv += " " + w;
		}

		return rv.substring(1);
	}

	public String toSerialRep() {		
		if (mWords == null || mWords.length == 0) return "(null)";

		String rv = "";

		for (TaggedWord w : mWords) {
			rv += " " + w.getWord();
		}

		return rv.substring(1);
	}

	public Entity resolve(Entity[] aResolutionContext) {
		/* do nothing; base entities are already resolved... */
		return this;
	}

	private TaggedWord[] mWords;
}
