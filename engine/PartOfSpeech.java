/*
 * NewsTerp Engine - We report.  You decipher.
 * copyright (c) 2007 Colin Bayer, Jack Hebert
 *
 * CSE 472 Spring 2007 final project
 */

import java.util.HashMap;

import edu.mit.jwi.item.*;

public enum PartOfSpeech {
	/* OpenNLP's English POS model uses the Penn Treebank POS tags, somewhat 
	   modified */

	/* ===== meta-parts-of-speech ===== */
	UNKNOWN			("???",		null, "part of speech unknown"),

	/* ===== punctuation ===== */
	STOP			(".", 		null, "sentence-final punctuation [? ! . ;]"),
	LEFT_PAREN		("-LRB-", 	null, "left parenthesis"),
	RIGHT_PAREN 	("-RRB-", 	null, "right parenthesis"),
	DASH			("--", 		null, "dash"),
	COMMA			(",", 		null, "comma"),
	COLON			(":",		null, "colon"),
	OPEN_DQUOTE		("``",		null, "open double quote"),
	CLOSE_DQUOTE	("''",		null, "close double quote"),
	CASH_MONEY		("$",		null, "dollar sign"),
	POUND			("#",		null, "pound sign"),
	SYMBOL			("SYM",		null, "miscellaneous symbol"),

	/* ===== closed-class words ===== */

	/* determiners: */
	SG_DET			("DT",		null, "determiner"),
	WH_DET			("WDT",		null, "wh-determiner [what, which]"),

	/* pronouns: */
	PERS_PN			("PRP",		null, "personal pronoun"),
	POSS_PN			("PRP$",	null, "possessive pronoun"),
	WH_PN			("WP",		null, "wh-pronoun [who, whom, which]"),
	POSS_WH_PN		("WP$",		null, "poss. wh-pronoun [whose]"), 

	/* conjunctions: */
	COORD_CONJ		("CC",		null, "coord. conjunction [and, or]"),

	/* prepositions: */
	P_SUBORD_CONJ	("IN",		null, "preposition/subordinating conjunction"),

	/* negative markers: */
	NOT				("*",		null, "not, n't"),

	/* auxiliaries and other T-elements: */
	MODAL_AUX		("MD",		null, "modal Aux [can, should, will]"),
	INFIN_TO		("TO",		null, "infinitive-marking to"),

	/* clitics: */
	POSS_CL			("POS",		null, "possessive ending ['s]"),

	/* quantifiers, etc.: */
	PREDET			("PDT",		null, "predeterminer [all, some, no]"),

	/* ===== open-class words ===== */

	/* nouns: */
	SG_MASS_N		("NN",		edu.mit.jwi.item.PartOfSpeech.NOUN, 
								"sg/mass noun"),
	PL_N			("NNS",		edu.mit.jwi.item.PartOfSpeech.NOUN, 
								"pl noun"),
	PROP_N			("NNP",		edu.mit.jwi.item.PartOfSpeech.NOUN, 
								"sg proper noun"),
	PL_PROP_N		("NNPS",	edu.mit.jwi.item.PartOfSpeech.NOUN, 
								"pl proper noun"),

	/* (numbers:)
	 * note: these don't always act as adjectives, but in light of where we use
	 * the JWI part of speech (in conjunction with searching for subcat frames)
	 * we treat it as one.
	 */
	CARDINAL_NO		("CD",		edu.mit.jwi.item.PartOfSpeech.ADJECTIVE,
								"cardinal number [1, two]"),
	ORDINAL_NO		("OD",		edu.mit.jwi.item.PartOfSpeech.ADJECTIVE,
								"ordinal number [1st, second]"),

	/* verbs: */
	V_BASE			("VB",		edu.mit.jwi.item.PartOfSpeech.VERB,
								"verb, base form [eat]"),
	V_PST			("VBD",		edu.mit.jwi.item.PartOfSpeech.VERB,
								"verb, past tense [ate]"),
	V_PRPL			("VBG",		edu.mit.jwi.item.PartOfSpeech.VERB,
								"verb, present participle/gerund [eating]"),
	V_PPL			("VBN",		edu.mit.jwi.item.PartOfSpeech.VERB,
								"verb, past participle [eaten]"),
	V_N_3SG_PRES	("VBP",		edu.mit.jwi.item.PartOfSpeech.VERB,
								"verb, non-3sg present [eat]"),
	V_3SG_PRES		("VBZ",		edu.mit.jwi.item.PartOfSpeech.VERB,
								"verb, 3sg present [eats]"),

	/* adjectives: */
	ADJ				("JJ",		edu.mit.jwi.item.PartOfSpeech.ADJECTIVE,
								"adjective"),
	COMP_ADJ		("JJR",		edu.mit.jwi.item.PartOfSpeech.ADJECTIVE,
								"comparative adj. [bigger]"),
	SEM_SUPER_ADJ	("JJS",		edu.mit.jwi.item.PartOfSpeech.ADJECTIVE,
								"superlative adj. [biggest]"),

	/* adverbs: */
	ADV				("RB",		edu.mit.jwi.item.PartOfSpeech.ADVERB,
								"adverb"),
	COMP_ADV		("RBR",		edu.mit.jwi.item.PartOfSpeech.ADVERB,
								"comparative adv. [livelier]"),
	SUPER_ADV		("RBS",		edu.mit.jwi.item.PartOfSpeech.ADVERB, 
								"superlative adv. [liveliest]"),
	ADV_PRT			("RP",		edu.mit.jwi.item.PartOfSpeech.ADVERB,
								"adverb/particle [about, off, up]"),
	WH_ADV			("WRB",		edu.mit.jwi.item.PartOfSpeech.ADVERB,
								"wh-adverb [how, where, when]"), 

	/* interjections: */
	INTERJ			("UH",		null, "interjection, exclamation"),

	/* ===== special words and exceptions ===== */

	/* existential there ("there is a hole in the sky through which things
	 * can fly...") */
	EX_THERE		("EX",		null, "existential there"),

	/* list markers */
	LIST_MARKER		("LS",		null, "list marker"),

	/* foreign words */
	FOREIGN			("FW",		null, "foreign word");

	private final String mTag;
	private final edu.mit.jwi.item.PartOfSpeech mWNPOS;
	private final String mLongType;

	private boolean mForeign;
	private boolean mCited;

	private PartOfSpeech(String aTag, edu.mit.jwi.item.PartOfSpeech aWNPOS, 
			String aLongType) {
		this.mTag = aTag;
		this.mWNPOS = aWNPOS;
		this.mLongType = aLongType;
	}

	public String toString() { return mTag; }
	public edu.mit.jwi.item.PartOfSpeech getWNPOS() { return mWNPOS; }
	public String getLongType() { return mLongType; }

	static private HashMap<String, PartOfSpeech> smTagdex;

	/*
	 * parse a string POS tag into its enumerated value; return null if no such
	 * tag is known.
	 */
	static PartOfSpeech parse(String aTag) {
		aTag = aTag.toUpperCase();

		/* check to see if we have a reverse string->POS index, and create one
		 * if not */
		if (smTagdex == null) {
			smTagdex = new HashMap<String, PartOfSpeech>();

			for (PartOfSpeech pos : values()) {
				smTagdex.put(pos.mTag, pos);
			}
		}

		return smTagdex.get(aTag);
	}
}
