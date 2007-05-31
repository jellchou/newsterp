/*
 * NewsTerp Engine - We report.  You decipher.
 * copyright (c) 2007 Colin Bayer, Jack Hebert
 *
 * CSE 472 Spring 2007 final project
 */

import java.io.*;
import java.util.*;

import java.util.ArrayList;

public class TaggedArticle {
	/* bug workaround: NLP throughput becomes unreasonably bad around 200
	 * articles processed.  restart all our NLP machinery every 200 articles. */
	private static int mArticlesProcessed = 0;

	public TaggedArticle(String aID, String aText) throws IOException {
		this.mID = aID;
		this.mSentences = new ArrayList<TaggedSentence>();
		NLPToolkitManager tkm = NLPToolkitManager.getInstance();

		if (++mArticlesProcessed % 200 == 0) {
			tkm.restart();
		}

		System.out.println("Processing file `" + aID + "'...");

		ArrayList<String> untagged_sents = new ArrayList<String>();

		// sentence-detect.
		System.out.print("Detecting sentences...");


		untagged_sents.addAll(Arrays.asList(tkm.getSD().sentDetect(aText)));


		System.out.println(" done (" + untagged_sents.size() + 
			" sentences).");

		// tokenize and tag.
		System.out.print("Tokenizing and tagging... *");

		for (String sent : untagged_sents) {
			System.out.print("\b|");
			String[] tokens = tkm.getTokenizer().tokenize(sent);

			System.out.print("\b-");
			TaggedSentence tagged = new TaggedSentence(tkm.getTagger(), tokens);

			mSentences.add(tagged);
		}

		System.out.println("\bdone.");

		// chunk.
		System.out.print("Chunking... ");
			
		ChunkerAdaptor ca = new ChunkerAdaptor(tkm.getChunker());
		ca.chunkify(this);

		System.out.println("done.");

		// do per-article fancy stuff here.
		/*
		System.out.println("All NPs in article: ");

		int i = 0;

		for (TaggedSentence s : getSentences()) {
			TaggedSentence.Chunk[] cks = s.getChunks(ChunkType.NP);

			System.out.println("Sentence " + i + ": " +
				Arrays.toString(cks));
			i++;
		}
		*/
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
