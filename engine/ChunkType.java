/*
 * NewsTerp Engine - We report.  You decipher.
 * copyright (c) 2007 Colin Bayer, Jack Hebert
 *
 * CSE 472 Spring 2007 final project
 */

public enum ChunkType {
	/* this list was taken from Tjong Kim Sang and Buchholz 2000:
	   http://acl.ldc.upenn.edu/W/W00/W00-0726.pdf */
	NONE		("(no chunk)"),
	NP			("noun phrase"),
	VP			("verb phrase"),
	PP			("prepositional phrase"),
	ADVP		("adverb phrase"),
	SBAR		("subordinated clause"),
	ADJP		("adjective phrase"),
	PRT			("particles"),
	CONJP		("conjunction phrase"),
	INTJ		("interjection"),
	LST			("list marker"),
	UCP			("unlike coordinated phrase");

	private ChunkType(String aLongType) {
		mLongType = aLongType;
	}

	private final String mLongType;

	public String getLongType() {
		return mLongType;
	}

	public static ChunkType parse(String aType) {
		try {
			return Enum.valueOf(ChunkType.class, aType);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
}
