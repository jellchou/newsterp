/*
 * NewsTerp Engine - We report.  You decipher.
 * copyright (c) 2007 Colin Bayer, Jack Hebert
 *
 * CSE 472 Spring 2007 final project
 */

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

import opennlp.tools.lang.english.*;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.postag.POSDictionary;

public class TaggedSentence {
	public TaggedSentence(PosTagger aTagger, String[] aWords) {
		if (aTagger == null) 
			throw new IllegalArgumentException("can't tag with a null tagger");

		ArrayList<TaggedWord> sentence =
			new ArrayList<TaggedWord>(aWords.length);

		if (aWords == null) return;

		/* tag words, convert the stringized tags into our PartOfSpeech type,
		   build TaggedWords... */
		String[] tags = aTagger.tag(aWords);

		for (int i = 0; i < aWords.length; i++) {
			PartOfSpeech pos = PartOfSpeech.parse(tags[i]);

			if (pos == null) {
				System.out.println("Warning: part-of-speech " + tags[i] + " is unknown! [in context " + aWords[i] + "/" + tags[i] + "]");
				pos = PartOfSpeech.UNKNOWN;
			}

			sentence.add(new TaggedWord(aWords[i], pos));
		}

		mSentence = sentence.toArray(new TaggedWord[0]);
		mChunks = new LinkedList<Chunk>();
	}

	public class Chunk {
		public Chunk(TaggedSentence aParent, int aFirstIdx, int aLastIdx, 
				ChunkType aType) {
			mParent = aParent;
			mFirstIdx = aFirstIdx;
			mLastIdx = aLastIdx;
			mType = aType;
		}

		public TaggedWord[] getWords() {
			return mParent.getWords(mFirstIdx, mLastIdx);
		}
		public ChunkType getType() { return mType; }
		public int getFirstIdx() { return mFirstIdx; }
		public int getLastIdx() { return mLastIdx; }

		public String toString() {
			String rv = "[" + mType;

			for (TaggedWord w : getWords()) {
				rv += " " + w;
			}

			return rv + "]";
		}

		private TaggedSentence mParent;
		private int mFirstIdx, mLastIdx;
		private ChunkType mType;
	}

	public TaggedWord[] getWords() { return mSentence.clone(); }

	public TaggedWord[] getWords(int aFirstIdx, int aLastIdx) {
		TaggedWord[] rv = new TaggedWord[aLastIdx - aFirstIdx + 1];

		for (int i = 0; i < rv.length; i++) {
			rv[i] = mSentence[aFirstIdx + i];
		}

		return rv;
	}

	/* notable chunk-list invariant: the chunk list is kept in ascending order
	 * by start word index.  this allows us to make judgments such as "the
	 * subject is the first NP in the sentence". */
	public Chunk[] getChunks() { return mChunks.toArray(new Chunk[0]); }

	public Chunk[] getChunks(ChunkType aType) {
		ArrayList<Chunk> l = new ArrayList<Chunk>(mChunks.size());

		for (Chunk ch : mChunks) {
			if (ch.getType() == aType) l.add(ch);
		}

		return l.toArray(new Chunk[0]);
	}

	public boolean addChunk(int aBeginIdx, int aEndIdx, ChunkType aType) {
		/* run through the chunk list, looking for an overlapping chunk... */
		ListIterator<Chunk> i = mChunks.listIterator();

		//System.out.print("adding chunk [" + aBeginIdx + ", " + aEndIdx +
		//	"]... ");

		while (i.hasNext()) {
			Chunk ck = i.next();

			if (ck.getLastIdx() >= aBeginIdx) {
				//System.out.println("failed (chunk [" + ck.getFirstIdx() +
				//	", " + ck.getLastIdx() + "] overlaps)");
				return false;
			}
			if (ck.getFirstIdx() >= aBeginIdx && ck.getFirstIdx() <= aEndIdx) {
				//System.out.println("failed (chunk [" + ck.getFirstIdx() +
				//	", " + ck.getLastIdx() + "] overlaps)");
				return false;
			}

			/* if the chunk is after the chunk being added, stop going through
			   the list; we can insert before it. */
			if (ck.getFirstIdx() > aEndIdx) break;
		}

		/* we point to the element after our insertion point, except in the
		 * case where hasPrevious() is false -- thus, we haven't iterated over
		 * any elements, so there were no elements to iterate over.  in other 
		 * words, if we have a previous element, go back to it. */
		if (i.hasPrevious()) i.previous();

		/* and insert. */
		i.add(new Chunk(this, aBeginIdx, aEndIdx, aType));

		//System.out.println("OK");

		return true;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < mSentence.length; i++) {
			sb.append(' ');
			sb.append(mSentence[i].mWord + "/" + mSentence[i].mPOS);
		}

		sb.deleteCharAt(0);
		return sb.toString();
	}

	public class TaggedWord {
		TaggedWord(String aWord, PartOfSpeech aPOS) {
			mWord = aWord; mPOS = aPOS;
		}

		public String getWord() { return mWord; }
		public PartOfSpeech getPOS() { return mPOS; }

		public String toString() { return mWord + "/" + mPOS; }

		private String mWord;
		private PartOfSpeech mPOS;
	}

	private TaggedWord[] mSentence;

	private LinkedList<Chunk> mChunks;
}
