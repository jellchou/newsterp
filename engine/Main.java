/*
 * NewsTerp Engine - We report.  You decipher.
 * copyright (c) 2007 Colin Bayer, Jack Hebert
 *
 * CSE 472 Spring 2007 final project
 */

import java.util.ArrayList;
import java.util.Arrays;

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
		for (; idx < aArgs.length; idx++) {
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

			// do fancy stuff here.
			System.out.println("All NPs in article: ");

			int i = 0;

			for (TaggedSentence s : art.getSentences()) {
				System.out.println("Sentence " + i + ": " +
					Arrays.toString(s.getChunks(ChunkType.NP)));
				i++;
			}
		}
	}
}
