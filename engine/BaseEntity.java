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

	public String toString() {
		if (mWords == null) return "(null)";

		String rv = "";

		for (TaggedWord w : mWords) {
			rv += " " + w;
		}

		return rv.substring(1);
	}

	public double similarity(Entity aCompareTo) {
		/* STUB: write a decent implementation of this. */
		return 1.0d;
	}

	public void resolve(Entity[] aResolutionContext) {
		/* do nothing; base entities are already resolved... */
	}

	private TaggedWord[] mWords;
}
