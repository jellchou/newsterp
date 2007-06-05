/*
 * NewsTerp Engine - We report.  You decipher.
 * copyright (c) 2007 Colin Bayer, Jack Hebert
 *
 * CSE 472 Spring 2007 final project
 */

import java.io.IOException;
import java.net.URL;

import opennlp.tools.lang.english.*;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.postag.POSDictionary;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.chunker.ChunkerME;

import edu.mit.jwi.dict.*;
import edu.mit.jwi.morph.*;

public class NLPToolkitManager {
    public static boolean init(
        	String aSentenceDetectorDB,
			String aTokenizerDB,
			String aTagDB, String aTagDict,
			String aChunkerDB,
			URL aWordNetDB) {
		if (smInstance != null) {
			smInstance = null;
		}

		try {
			smInstance = new NLPToolkitManager(aSentenceDetectorDB, aTokenizerDB, aTagDB,
				aTagDict, aChunkerDB, aWordNetDB);
		} catch (Exception e) {
			System.err.println("Error creating NLP objects (" + e + ")...");
			return false;
		}

		return true;
	}

	public static NLPToolkitManager getInstance() {
        return smInstance;
	}

	public SentenceDetectorME getSD() { return mSD; }
	public TokenizerME getTokenizer() { return mTokenizer; }
	public POSTaggerME getTagger() { return mTagger; }
	public ChunkerME getChunker() { return mChunker; }
	public Dictionary getDictionary() { return mDict; }
	public WordnetStemmer getStemmer() { return mStemmer; }

	private NLPToolkitManager(
			String aSentenceDetectorDB,
			String aTokenizerDB,
			String aTagDB, String aTagDict,
			String aChunkerDB,
			URL aWordNetDB) throws IOException {
		mSentenceDetectorDB = aSentenceDetectorDB;
		mTokenizerDB = aTokenizerDB;
		mTagDB = aTagDB;
		mTagDict = aTagDict;
		mChunkerDB = aChunkerDB;
		mWordNetDB = aWordNetDB;

		restart();
	}

	public void restart() throws IOException {
		System.out.println("Building sentence detector...");

		mSD = new SentenceDetector(mSentenceDetectorDB);

		System.out.println("Building tokenizer...");

		mTokenizer = new Tokenizer(mTokenizerDB);

		System.out.println("Building POS tagger...");

		mTagger = new PosTagger(mTagDB, new POSDictionary(mTagDict));

		System.out.println("Building chunker...");

		mChunker = new TreebankChunker(mChunkerDB);

		System.out.println("Building WordNet database...");

		mDict = new Dictionary(mWordNetDB);
		mDict.open();

		mStemmer = new WordnetStemmer(mDict);
	}

	private String mSentenceDetectorDB, mTokenizerDB, mTagDB, mTagDict, 
		mChunkerDB;
	private URL mWordNetDB;

	SentenceDetectorME mSD;
	TokenizerME mTokenizer;
	POSTaggerME mTagger;
	ChunkerME mChunker;
	Dictionary mDict;
	WordnetStemmer mStemmer;

    private static NLPToolkitManager smInstance;
}
