/*
 * NewsTerp Engine - We report.  You decipher.
 * copyright (c) 2007 Colin Bayer, Jack Hebert
 *
 * CSE 472 Spring 2007 final project
 */

import java.lang.reflect.Array;

import java.util.*;
import java.io.*;

import opennlp.tools.lang.english.*;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.postag.POSDictionary;

public class Main {
	public static void usage() {
		System.err.println(
			"Usage: java Main [-nlp <dir>] <file1> <file2> ... <fileN>\n" +
			"\t-nlp <dir>:\t OpenNLP Tools root directory\n"
		);
	}

	public static void main(String[] aArgs) {
		int idx = 0;
		String nlp_path = ".";

		while (idx < aArgs.length) {
			if (aArgs[idx].equals("-nlp")) {
				nlp_path = aArgs[idx + 1];
				idx += 2;
				continue;
			} else if (aArgs[idx].equals("-h")) {
				usage();
				return;
			} else {
				/* idx now points to the first file in the arg list */
				break;
			}
		}

		if (aArgs.length - idx <= 0) {
			/* we need at least one file to extract main ideas from. */
			usage();
			return;
		}

		SentenceDetector sd;
		Tokenizer tok;
		PosTagger tgr;
		TreebankChunker chk;

		/* build OpenNLP processing objects */
		try {
			System.out.println("Building sentence detector...");

			sd = new SentenceDetector(
				nlp_path + "/models/english/sentdetect/EnglishSD.bin.gz"
			);

			System.out.println("Building tokenizer...");

			tok = new Tokenizer(
				nlp_path + "/models/english/tokenize/EnglishTok.bin.gz"
			);

			System.out.println("Building POS tagger...");

			tgr = new PosTagger(
				nlp_path + "/models/english/parser/tag.bin.gz",
				new POSDictionary(nlp_path + "/models/english/parser/tagdict"));

			System.out.println("Building chunker...");

			chk = new TreebankChunker(
				nlp_path + "/models/english/chunker/EnglishChunk.bin.gz"
			);
		} catch (Exception e) {
			System.out.println("Couldn't create OpenNLP objects (" + e + 
				"); exiting");
			return;
		}

		/* provide space to store the processed articles... */
		TaggedArticle[] articles = new TaggedArticle[aArgs.length - idx];

		/* chop up and tag all of our articles. */
		for (int n = 0; idx < aArgs.length; idx++, n++) {
			System.out.println("Processing file `" + aArgs[idx] + "'...");

			ArrayList<String> paras = new ArrayList<String>();
			ArrayList<String> untagged_sents = new ArrayList<String>();

			System.out.print("Reading...");

			// read file.
			try {
				StringBuffer para = new StringBuffer();
				BufferedReader rdr = new BufferedReader(
					new FileReader(aArgs[idx]));

				for (String line = ""; line != null; line = rdr.readLine()) {
					if (line.equals("")) {
						if (para.length() != 0) {
							paras.add(para.toString());
							para.setLength(0);
							System.out.print('.');
						}
					} else {
						para.append(line).append(" ");
					}
				}

				if (para.length() != 0) {
					paras.add(para.toString());
					System.out.print('.');
				}

				System.out.println(" done (" + paras.size() + " paras).");
			} catch (IOException e) {
				System.out.println("Error while reading (" + e +
					"); skipping...");

				continue;
			}

			// sentence-detect.
			System.out.print("Detecting sentences...");

			for (String para : paras) {
				untagged_sents.addAll(Arrays.asList(sd.sentDetect(para)));
			}

			System.out.println(" done (" + untagged_sents.size() + 
				" sentences).");

			// tokenize and tag.
			System.out.print("Tokenizing and tagging... *");

			TaggedArticle art = new TaggedArticle(aArgs[idx]);
			articles[n] = art;

			for (String sent : untagged_sents) {
				System.out.print("\b|");
				String[] tokens = tok.tokenize(sent);

				System.out.print("\b-");
				TaggedSentence tagged = new TaggedSentence(tgr, tokens);

				art.append(tagged);
			}

			System.out.println("\bdone.");

			// chunk.
			System.out.print("Chunking... ");
			
			ChunkerAdaptor ca = new ChunkerAdaptor(chk);
			ca.chunkify(art);

			System.out.println("done.");

			//System.out.println(art);

			// do per-article fancy stuff here.
			System.out.println("All NPs in article: ");

			int i = 0;

			for (TaggedSentence s : art.getSentences()) {
				TaggedSentence.Chunk[] cks = s.getChunks(ChunkType.NP);

				System.out.println("Sentence " + i + ": " +
					Arrays.toString(cks));
				i++;
			}
		}

		// do per-article-set fancy stuff here.
		System.out.println("Most popular 5 NPs in article set:");

		HashMap<TaggedSentence.Chunk, Integer> pop_index = 
			new HashMap<TaggedSentence.Chunk, Integer>();

		for (TaggedArticle a : articles) {
			for (TaggedSentence s : a.getSentences()) {
				for (TaggedSentence.Chunk ck : s.getChunks(ChunkType.NP)) {
					Integer ck_ct = null;

					if ((ck_ct = pop_index.get(ck)) != null) {
						pop_index.put(ck, new Integer(ck_ct.intValue() + 1));
					} else {
						pop_index.put(ck, new Integer(1));
					}
				}
			}
		}

		// don't ask why Java doesn't let you make genericized arrays. just
		// accept that this line works, and move on.
		Map.Entry<TaggedSentence.Chunk, Integer>[] pop_entries =
			(Map.Entry<TaggedSentence.Chunk, Integer>[]) new Map.Entry[0];

		pop_entries = pop_index.entrySet().toArray(pop_entries);

		Arrays.sort(pop_entries, 
			new Comparator< Map.Entry<TaggedSentence.Chunk, Integer> > () {
				public int 
					compare(Map.Entry<TaggedSentence.Chunk, Integer> aA,
							Map.Entry<TaggedSentence.Chunk, Integer> aB) {
					return aA.getValue().compareTo(aB.getValue());
				}
			}
		);

		for (int i = pop_entries.length - 1; i > pop_entries.length - 6; i--) {
			System.out.println(pop_entries[i].getKey() + " (" + 
				pop_entries[i].getValue() + ")");
		}
	}
}
