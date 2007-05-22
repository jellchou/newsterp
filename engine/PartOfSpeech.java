/*
 * NewsTerp Engine - We report.  You decipher.
 * copyright (c) 2007 Colin Bayer, Jack Hebert
 *
 * CSE 472 Spring 2007 final project
 */

import java.util.HashMap;

public enum PartOfSpeech {
	/* OpenNLP's English POS model uses the Penn Treebank POS tags, somewhat 
	   modified */

	/* ===== meta-parts-of-speech ===== */
	UNKNOWN			("???",		"part of speech unknown"),

	/* ===== punctuation ===== */
	STOP			(".", 		"sentence-final punctuation [? ! . ;]"),
	LEFT_PAREN		("-LRB-", 	"left parenthesis"),
	RIGHT_PAREN 	("-RRB-", 	"right parenthesis"),
	DASH			("--", 		"dash"),
	COMMA			(",", 		"comma"),
	COLON			(":",		"colon"),
	OPEN_DQUOTE		("``",		"open double quote"),
	CLOSE_DQUOTE	("''",		"close double quote"),
	CASH_MONEY		("$",		"dollar sign"),

	/* ===== closed-class words ===== */

	/* determiners: */
	SG_DET			("DT",		"determiner"),
	WH_DET			("WDT",		"wh-determiner [what, which]"),

	/* pronouns: */
	PERS_PN			("PRP",		"personal pronoun"),
	POSS_PN			("PRP$",	"possessive pronoun"),
	WH_PN			("WP",		"wh-pronoun [who, whom, which]"),
	POSS_WH_PN		("WP$",		"poss. wh-pronoun [whose]"), 

	/* conjunctions: */
	COORD_CONJ		("CC",		"coord. conjunction [and, or]"),

	/* prepositions: */
	P_SUBORD_CONJ	("IN",		"preposition/subordinating conjunction"),

	/* negative markers: */
	NOT				("*",		"not, n't"),

	/* auxiliaries and other T-elements: */
	MODAL_AUX		("MD",		"modal Aux [can, should, will]"),
	INFIN_TO		("TO",		"infinitive-marking to"),

	/* clitics: */
	POSS_CL			("POS",		"possessive ending ['s]"),

	/* ===== open-class words ===== */

	/* nouns: */
	SG_MASS_N		("NN",		"sg/mass noun"),
	PL_N			("NNS",		"pl noun"),
	PROP_N			("NNP",		"sg proper noun"),
	PL_PROP_N		("NNPS",	"pl proper noun"),

	/* (numbers:) */
	CARDINAL_NO		("CD",		"cardinal number [1, two]"),
	ORDINAL_NO		("OD",		"ordinal number [1st, second]"),

	/* verbs: */
	V_BASE			("VB",		"verb, base form [eat]"),
	V_PST			("VBD",		"verb, past tense [ate]"),
	V_PRPL			("VBG",		"verb, present participle/gerund [eating]"),
	V_PPL			("VBN",		"verb, past participle [eaten]"),
	V_N_3SG_PRES	("VBP",		"verb, non-3sg present [eat]"),
	V_3SG_PRES		("VBZ",		"verb, 3sg present [eats]"),

	/* adjectives: */
	ADJ				("JJ",		"adjective"),
	COMP_ADJ		("JJR",		"comparative adj. [bigger]"),
	SEM_SUPER_ADJ	("JJS",		"superlative adj. [biggest]"),

	/* adverbs: */
	ADV				("RB",		"adverb"),
	COMP_ADV		("RBR",		"comparative adv. [livelier]"),
	SUPER_ADV		("RBS",		"superlative adv. [liveliest]"),
	ADV_PRT			("RP",		"adverb/particle [about, off, up]"),
	WH_ADV			("WRB",		"wh-adverb [how, where, when]"), 

	/* interjections: */
	INTERJ			("UH",		"interjection, exclamation"),

	/* ===== special words and exceptions ===== */

	/* existential there ("there is a hole in the sky through which things
	 * can fly...") */
	EX_THERE		("EX",		"existential there");

	private final String mTag;
	private final String mLongType;

	private boolean mForeign;
	private boolean mCited;

	private PartOfSpeech(String aTag, String aLongType) {
		this.mTag = aTag;
		this.mLongType = aLongType;
	}

	public String toString() { return mTag; }
	public String getLongType() { return mLongType; }

	static private HashMap<String, PartOfSpeech> smTagdex;
	/*
	 * parse a string POS tag into its enumerated value; return null if no such
	 * tag is known.
    * FW foreign word (hypenated before regular tag)
    * NC cited word (hyphenated after regular tag)
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
