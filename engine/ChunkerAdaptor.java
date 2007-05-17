/*
 * NewsTerp Engine - We report.  You decipher.
 * copyright (c) 2007 Colin Bayer, Jack Hebert
 *
 * CSE 472 Spring 2007 final project
 */

import opennlp.tools.lang.english.*;

import opennlp.tools.chunker.ChunkerME;

public class ChunkerAdaptor {
	public ChunkerAdaptor(ChunkerME aChunker) {
		mChunker = aChunker;
	}

	void chunkify(TaggedArticle aArt) {
		for (TaggedSentence s : aArt.getSentences()) {
			chunkify(s);
		}
	}

	void chunkify(TaggedSentence aSent) {
		/* grab a word list from the sentence and break it into words and
		 * tags for OpenNLP. */
		TaggedWord[] words = aSent.getWords();

		String[] clean_words = new String[words.length];
		String[] clean_tags = new String[words.length];

		for (int i = 0; i < words.length; i++) {
			clean_words[i] = words[i].getWord();
			clean_tags[i] = words[i].getPOS().toString();
		}

		/* chunk! */
		String[] chunk_tags = mChunker.chunk(clean_words, clean_tags);

		/* convert from the chunk tag system in the ConLL-2000 paper to
		   more-reasonable span/type data */
		ChunkType current_chunk = null;
		int current_begin = -1;	

		for (int i = 0; i < chunk_tags.length; i++) {
			//System.out.print(chunk_tags[i] + " ");

			if (chunk_tags[i].equals("O")) {
				if (current_chunk != null)
					aSent.addChunk(current_begin, i - 1, current_chunk);

				current_chunk = null;
			} else if (chunk_tags[i].substring(0, 2).equals("B-")) {
				if (current_chunk != null)
					aSent.addChunk(current_begin, i - 1, current_chunk);

				current_begin = i;
				current_chunk = ChunkType.parse(chunk_tags[i].substring(2));
			} else if (chunk_tags[i].substring(0, 2).equals("I-")) {
				/* do nothing */
			} else {
				System.out.println("Warning: unexpected chunk tag " + 
					chunk_tags[i] + "!");
			}
		}

		/* make sure to add any chunk running to the end of the sentence. */
		if (current_chunk != null)
			aSent.addChunk(current_begin, chunk_tags.length - 1, current_chunk);
	}

	private ChunkerME mChunker;
}
