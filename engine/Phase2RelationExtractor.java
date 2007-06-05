/*
 * NewsTerp Engine - We report.  You decipher.
 * copyright (c) 2007 Colin Bayer, Jack Hebert
 *
 * CSE 472 Spring 2007 final project
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

import edu.mit.jwi.dict.*;
import edu.mit.jwi.item.*;
import edu.mit.jwi.morph.*;

public class Phase2RelationExtractor implements RelationExtractor {
	public Phase2RelationExtractor() { mP1RE = new Phase1RelationExtractor(); }

	private Phase1RelationExtractor mP1RE;

	private static TaggedWord[] __expl_words = { 
		new TaggedWord("<EXPL>", PartOfSpeech.SG_MASS_N) };
	private static Entity EXPLETIVE_ENTITY = new BaseEntity(__expl_words);

	private int firstChunkOfType(TaggedSentence.Chunk[] aChunks, 
			ChunkType aType) {
		for (int i = 0; i < aChunks.length; i++) {
			if (aChunks[i].getType() == aType) return i;
		}

		return -1;
	}

	private int firstChunkOfType(TaggedSentence.Chunk[] aChunks, 
			ChunkType aType, int aStart) {
		for (int i = aStart; i < aChunks.length; i++) {
			if (aChunks[i].getType() == aType) return i;
		}

		return -1;
	}

	Relation[] extractFromClause(TaggedSentence.Chunk[] aChunks) {
		ArrayList<Relation> main_rels = new ArrayList<Relation>();
		TaggedSentence.Chunk pred = null;
		TaggedSentence.Chunk[] left = null, right = null;

		/* find predicate chunk by searching for a VP in the chunk list */
		int pred_idx = firstChunkOfType(aChunks, ChunkType.VP);

		/* if no predicate found, print sentence, return null, and skip. */
		if (pred_idx == -1) {
			//System.out.println("no predicate in " + Arrays.toString(aChunks) + 
			//	"?");
			return new Relation[0];
		} else {
			pred = aChunks[pred_idx];
			/* split the remainder of the sentence into "left" and "right"
			   components, based on which side of the predicate they lie
			   on */
			List<TaggedSentence.Chunk> l = Arrays.asList(aChunks);
			left = l.subList(0, pred_idx).toArray(new TaggedSentence.Chunk[0]);
			right = l.subList(pred_idx + 1, l.size()).
					toArray(new TaggedSentence.Chunk[0]);

			System.out.print("guessing predicate chunk is " + pred + 
				"... ");
		}


		/* try and determine the most important word in the predicate using
		   this heuristic: choose the rightmost verb, unless the predicate
		   contains an infinitive-marking to (in a position other than the 
		   first); then choose the rightmost verb to the left of the to. */
		TaggedWord[] pred_words = pred.getWords();
		TaggedWord pred_core = null;

		for (int i = 0; i < pred_words.length; i++) {
			if (pred_words[i].getPOS() == PartOfSpeech.INFIN_TO) {
				if (i != 0) break;
			} else if (pred_words[i].getPOS().getWNPOS() == 
					edu.mit.jwi.item.PartOfSpeech.VERB) {
				pred_core = pred_words[i];
			}
		}

		/* if no verbs, choose the rightmost word and hope for the best... */
		if (pred_core == null) pred_core = pred_words[pred_words.length - 1];

		System.out.print("predcore is " + pred_core + "... ");

		/* try and stem it. */
		WordnetStemmer stmr = NLPToolkitManager.getInstance().getStemmer();
		String[] stems = stmr.getRoots(pred_core.getWord(), 
			edu.mit.jwi.item.PartOfSpeech.VERB).toArray(new String[0]);

		System.out.println("stems are " + Arrays.toString(stems) + "... ");

		/* look it up in WordNet... */
		LinkedList<IWord> words = new LinkedList<IWord>();
		Dictionary dict = NLPToolkitManager.getInstance().getDictionary();

		for (String stem : stems) {
			edu.mit.jwi.item.PartOfSpeech pos =
				pred_core.getPOS().getWNPOS();

			if (pos == null) continue;

			IIndexWord idx = dict.getIndexWord(stem, pos);

			if (idx != null) {
				for (IWordID id : idx.getWordIDs()) {
					IWord wd = dict.getWord(id);

					if (wd != null) words.add(wd);
				}
			}
		}

		if (words.isEmpty()) {
			System.out.println("no words found for predicate core " + 
				pred_core + "; falling back to phase 1 extractor...");
			return mP1RE.extractFromClause(aChunks);
		}

		/* compute relations for every possible verb frame compatible with this
		   verb. */
		EnumMap<VerbFrame, Relation[]> possible_rels = 
			new EnumMap<VerbFrame, Relation[]>(VerbFrame.class);

		for (IWord wd : words) {
			IVerbFrame[] frames = wd.getVerbFrames();

			if (frames == null) {
				System.out.println("no verb frames listed for predicate core;" +
					" falling back to phase 1 extractor...");
				return mP1RE.extractFromClause(aChunks);
			}

			for (IVerbFrame frame : wd.getVerbFrames()) {
				if (!(frame instanceof VerbFrame)) {
					System.out.println("expected a VerbFrame!");
				} else {
					VerbFrame f = (VerbFrame)frame;

					if (possible_rels.get(f) == null) {
						/* relation hasn't been computed yet, do so now. */
						possible_rels.put(f, decomposeClause(left, right, f,
							new Predicate(pred.getWords())));
					}
				}
			}
		}

		LinkedList<Relation> final_rels = new LinkedList<Relation>();

		for (Relation[] relset : possible_rels.values()) {
			if (relset != null)
				final_rels.addAll(Arrays.asList(relset));
		}

		return final_rels.toArray(new Relation[0]);
	}

	private Relation[] decomposeClause(TaggedSentence.Chunk[] aLeftChunks,
			TaggedSentence.Chunk[] aRightChunks, VerbFrame aFrame, 
			Predicate aPred) {
		Relation[] rv = null;
		Entity subj;
		Entity[] objs = null;
		TaggedSentence.Chunk[] subj_chunks, obj_chunks;
		int end_objs_idx;

		/* handle subject; assume the subject is the first NP in the set of
		   chunks left of the predicate. */
		int subj_ck_idx = firstChunkOfType(aLeftChunks, ChunkType.NP);

		if (subj_ck_idx != -1) {
			subj_chunks = new TaggedSentence.Chunk[1];
			subj_chunks[0] = aLeftChunks[subj_ck_idx];
		} else {
			/* no NPs to the left of the predicate.  curious. */
			subj_chunks = new TaggedSentence.Chunk[0];
		}

		if (aFrame == VerbFrame.NUM_03 || aFrame == VerbFrame.NUM_34) {
			/* frames 3 and 34 have expletive subject; if the subject chunk is
			   contentful (i.e., not "it"), reject this frame by returning no
			   relations. */
			if (subj_chunks.length != 1 || 
				subj_chunks[0].getWords().length != 1 ||
				!subj_chunks[0].getWords()[0].getWord().
					equalsIgnoreCase("it")) {
				/* rejected! */
				//System.out.println("Rejected subject " + 
				//	Arrays.toString(subj_chunks) + " for predicate " + aPred +
				//	" and verb frame " + aFrame);
				return new Relation[0];
			}

			subj = EXPLETIVE_ENTITY;
		} else {
			/* every other frame has a normal NP subject.  coolness. */
			ArrayList<TaggedWord> subj_words = new ArrayList<TaggedWord>();

			for (TaggedSentence.Chunk ck : subj_chunks) {
				subj_words.addAll(Arrays.asList(ck.getWords()));
			}

			if (subj_words.isEmpty()) {
				//System.out.println("Non-expletive sentence with null subject!");
				return new Relation[0];
			} else {
				subj = new BaseEntity(subj_words.toArray(new TaggedWord[0]));
			}
		}

		/* handle object(s):
		   =========== Intransitive ========== */
		if (aFrame == VerbFrame.NUM_01 || aFrame == VerbFrame.NUM_02 ||
				aFrame == VerbFrame.NUM_23 || aFrame == VerbFrame.NUM_28 || 
				aFrame == VerbFrame.NUM_32 || aFrame == VerbFrame.NUM_35) {
			/* frames 1, 2, and 23 are intransitive; they take no arguments. */
			objs = new Entity[0];
			end_objs_idx = 0;
		} else 
			/* =========== Transitive ========== */
			if (aFrame == VerbFrame.NUM_08 || aFrame == VerbFrame.NUM_09 || 
				aFrame == VerbFrame.NUM_10 || aFrame == VerbFrame.NUM_11) {
			/* frames 8, 9, 10, and 11 take NP objects. */
			int obj_idx = firstChunkOfType(aRightChunks, ChunkType.NP);

			if (obj_idx == -1) {
				//System.out.println("Transitive predicate " + aPred + 
				//	" in verb frame " + aFrame + 
				//	" given 0 arguments; rejected");
				return new Relation[0];
			} else {
				objs = new Entity[1];
				objs[0] = new BaseEntity(aRightChunks[obj_idx].getWords());
				end_objs_idx = obj_idx + 1;
			}
		} else if (aFrame == VerbFrame.NUM_04 || aFrame == VerbFrame.NUM_12 ||
				aFrame == VerbFrame.NUM_13 || aFrame == VerbFrame.NUM_22 ||
				aFrame == VerbFrame.NUM_27) {
			/* frames 4, 12, 13, 22, and 27 expect PP objects. */
			int obj_idx = firstChunkOfType(aRightChunks, ChunkType.PP);

			if (obj_idx == -1) {
				//System.out.println("PP-transitive predicate " + aPred +
				//	" in verb frame " + aFrame + " given no PP args; rejected");
				return new Relation[0];
			}

			/* PPs contain only the preposition, according to Kim Sang and
			   Buchholz 2000; check for selectional constraints, and reject if
			   not met. */
			TaggedWord[] pp = aRightChunks[obj_idx].getWords();

			if (/* frames 12 and 27 require a to-PP. */
				((aFrame == VerbFrame.NUM_12 || aFrame == VerbFrame.NUM_27) &&
					(pp.length != 1 || 
						!pp[0].getWord().equalsIgnoreCase("to"))) ||
				/* frame 13 requires an on-PP. */
				(aFrame == VerbFrame.NUM_13 &&
					(pp.length != 1 || 
						!pp[0].getWord().equalsIgnoreCase("on")))) {
				//System.out.println("PP-transitive predicate given invalid PP " 
				//	+ Arrays.toString(pp) + " in verb frame " + aFrame + 
				//	"; rejected.");

				return new Relation[0];
			} else if (obj_idx + 1 < aRightChunks.length) {
				/* PP is valid for purposes of the predicate; use next chunk as
				   argument of preposition, and combine into object. */
				TaggedWord[] obj_words = new TaggedWord[pp.length +
					aRightChunks[obj_idx + 1].getWords().length];

				for (int i = 0; i < pp.length; i++) {
					obj_words[i] = pp[i];
				}

				TaggedWord[] next = aRightChunks[obj_idx + 1].getWords();
	
				for (int i = pp.length; i < obj_words.length; i++) {
					obj_words[i] = next[i - pp.length];
				}

				objs = new Entity[1];
				objs[0] = new BaseEntity(obj_words);
				end_objs_idx = obj_idx + 2;
			} else {
				objs = new Entity[1];
				objs[0] = new BaseEntity(aRightChunks[obj_idx].getWords());
				end_objs_idx = obj_idx + 1;
			}
		} else if (aFrame == VerbFrame.NUM_06) {
			/* frame 6 expects AdjP/NP objects; we arbitrarily prioritize NP */
			int obj_idx_np = firstChunkOfType(aRightChunks, ChunkType.NP),
				obj_idx_adjp = firstChunkOfType(aRightChunks, ChunkType.ADJP);

			if (obj_idx_np == -1) {
				if (obj_idx_adjp == -1) {
					//System.out.println("NP/AdjP-transitive predicate " + aPred +
					//	" in verb frame " + aFrame + 
					//	" given 0 arguments; rejected");
					return new Relation[0];
				} else {
					objs = new Entity[1];
					objs[0] =
						new BaseEntity(aRightChunks[obj_idx_adjp].getWords());
					end_objs_idx = obj_idx_adjp + 1;
				}
			} else {
				objs = new Entity[1];
				objs[0] =
					new BaseEntity(aRightChunks[obj_idx_np].getWords());
				end_objs_idx = obj_idx_np + 1;
			}
		} else if (aFrame == VerbFrame.NUM_07) {
			/* frame 7 expects AdjP objects. */
			int obj_idx = firstChunkOfType(aRightChunks, ChunkType.ADJP);

			if (obj_idx == -1) {
				//System.out.println("AdjP-transitive predicate " + aPred +
				//	" in verb frame " + aFrame + 
				//	" given 0 arguments; rejected");
				return new Relation[0];
			} else {
				/* room for improvement: NP-containing AdjPs are considered
				   multiple chunks.  merge them in. */
				objs = new Entity[1];
				objs[0] = new BaseEntity(aRightChunks[obj_idx].getWords());
				end_objs_idx = obj_idx + 1;
			}
		} else if (aFrame == VerbFrame.NUM_33) {
			/* frame 33 expects a VP object (more specifically, a gerund) */
			int obj_idx = firstChunkOfType(aRightChunks, ChunkType.VP);

			if (obj_idx == -1) {
				//System.out.println("VP-transitive predicate " + aPred +
				//	" in verb frame " + aFrame + " given 0 arguments; rejected");
				return new Relation[0];
			} else {
				objs = new Entity[1];
				objs[0] = new BaseEntity(aRightChunks[obj_idx].getWords());
				end_objs_idx = obj_idx + 1;
			}
		} else if (aFrame == VerbFrame.NUM_26 ||
				aFrame == VerbFrame.NUM_29 ||
				aFrame == VerbFrame.NUM_34) {
			/* frames 26, 28, 29, 32, 34, and 35 expect subordinate clause 
			   objects of varying finiteness and complementizers. we don't 
			   check finiteness, but we do check complementizers. 

			   frames 28, 32, and 35 are technically transitive, but they are
			   handled above in the intransitive case because the infinitives
			   they take are chunked as part of the VP.  */

			/* room for improvement: we don't handle SBAR-free subordinate
			   clauses well (or at all, for that matter). */
			int obj_idx = firstChunkOfType(aRightChunks, ChunkType.SBAR);

			TaggedSentence.Chunk[] subord_cks =
				Arrays.asList(aRightChunks).subList(obj_idx + 1, 
					aRightChunks.length).toArray(new TaggedSentence.Chunk[0]);

			Relation[] subord_rels = extractFromClause(subord_cks);

			/* fixme: we only use the first relation extracted from the
			   subordinate clause. */
			if (subord_rels.length >= 2) {
				System.out.println("discarding " + (subord_rels.length - 1) + 
					" subordinate relations...");
			} else if (subord_rels.length == 0) {
				//System.out.println("subordinate clause returned no relations;" +
				//	" rejecting");
				return new Relation[0];
			}

			objs = new Entity[1];
			objs[0] = new RelationEntity(subord_rels[0]);
			/* room for improvement: we assume that the subordinate clause
			   consumes the remainder of the sentence. */
			end_objs_idx = aRightChunks.length;
		} else
			/* ========== Ditransitive =========== */
			if (aFrame == VerbFrame.NUM_05) {
			/* frame 5 takes an NP argument followed by an AdjP or NP 
			   argument. */
			int obj_idx_1 = firstChunkOfType(aRightChunks, ChunkType.NP);

			if (obj_idx_1 == -1) {
				//System.out.println("NP + AdjP/NP ditransitive passed 0 args; " +
				//	"rejecting");
				return new Relation[0];
			}

			int obj_idx_2_np = firstChunkOfType(aRightChunks, ChunkType.NP, 
					obj_idx_1 + 1),
				obj_idx_2_adjp = firstChunkOfType(aRightChunks, ChunkType.ADJP,
					obj_idx_1 + 1);

			if (obj_idx_2_np == -1) {
				if (obj_idx_2_adjp == -1) {
					//System.out.println("NP + NP/AdjP ditransitive predicate " + 
					//	aPred + " in verb frame " + aFrame + 
					//	" given 1 argument; rejected");
					return new Relation[0];
				} else {
					objs = new Entity[2];
					objs[0] = new BaseEntity(aRightChunks[obj_idx_1].getWords());
					objs[1] =
						new BaseEntity(aRightChunks[obj_idx_2_adjp].getWords());
					end_objs_idx = obj_idx_2_adjp + 1;
				}
			} else {
				objs = new Entity[2];
				objs[0] = new BaseEntity(aRightChunks[obj_idx_1].getWords());
				objs[1] = new BaseEntity(aRightChunks[obj_idx_2_np].getWords());
				end_objs_idx = obj_idx_2_np + 1;
			}
		} else if (aFrame == VerbFrame.NUM_14) {
			/* frame 14 is the "inverted" two-NP ditransitive. */
			int obj_idx_1 = firstChunkOfType(aRightChunks, ChunkType.NP),
				obj_idx_2;

			if (obj_idx_1 == -1) {
				//System.out.println("2xNP ditransitive given 0 args; rejecting");
				return new Relation[0];
			} else if ((obj_idx_2 = firstChunkOfType(aRightChunks, 
							ChunkType.NP, obj_idx_1 + 1)) == -1) {
				//System.out.println("2xNP ditransitive given 1 arg; rejecting");
				return new Relation[0];
			}

			objs = new Entity[2];
			/* note: the objects of this ditransitive are flipped with respect
			   to all other English ditransitives; the target precedes the
			   theme. */
			objs[0] = new BaseEntity(aRightChunks[obj_idx_2].getWords());
			objs[1] = new BaseEntity(aRightChunks[obj_idx_1].getWords());
			end_objs_idx = obj_idx_2 + 1;
		} else if (aFrame == VerbFrame.NUM_15 || aFrame == VerbFrame.NUM_16 ||
				aFrame == VerbFrame.NUM_17 || aFrame == VerbFrame.NUM_18 ||
				aFrame == VerbFrame.NUM_19 || aFrame == VerbFrame.NUM_20 ||
				aFrame == VerbFrame.NUM_21 || aFrame == VerbFrame.NUM_31) {
			/* verb frames 15-21 and 31 are NP-PP ditransitives. */
			int obj_idx_1 = firstChunkOfType(aRightChunks, ChunkType.NP),
				obj_idx_2;

			if (obj_idx_1 == -1) {
				//System.out.println("NP-PP ditransitive given 0 args; rejecting");
				return new Relation[0];
			} else if ((obj_idx_2 = firstChunkOfType(aRightChunks, 
							ChunkType.PP, obj_idx_1 + 1)) == -1) {
				//System.out.println("NP-PP ditransitive given 1 arg; rejecting");
				return new Relation[0];
			}

			/* check PP for selectional restriction. */
			TaggedWord[] pp_words = aRightChunks[obj_idx_2].getWords();

			if ((aFrame == VerbFrame.NUM_15 && 
					(pp_words.length != 1 || 
					 !pp_words[0].getWord().equalsIgnoreCase("to"))) ||
				(aFrame == VerbFrame.NUM_16 && 
					(pp_words.length != 1 || 
					 !pp_words[0].getWord().equalsIgnoreCase("from"))) ||
				((aFrame == VerbFrame.NUM_17 || aFrame == VerbFrame.NUM_31) && 
					(pp_words.length != 1 || 
					 !pp_words[0].getWord().equalsIgnoreCase("with"))) ||
				(aFrame == VerbFrame.NUM_18 && 
					(pp_words.length != 1 || 
					 !pp_words[0].getWord().equalsIgnoreCase("of"))) ||
				(aFrame == VerbFrame.NUM_19 && 
					(pp_words.length != 1 || 
					 !pp_words[0].getWord().equalsIgnoreCase("on")))) {
				//System.out.println("Invalid preposition " + 
				//	Arrays.toString(pp_words) + " to verb frame " + aFrame +
				//	"; rejecting");
				return new Relation[0];
			} else if (obj_idx_2 + 1 < aRightChunks.length) {
				/* PP is valid for purposes of the predicate; use next chunk as
				   argument of preposition, and combine into object. */
				TaggedWord[] obj_words = new TaggedWord[pp_words.length +
					aRightChunks[obj_idx_2 + 1].getWords().length];

				for (int i = 0; i < pp_words.length; i++) {
					obj_words[i] = pp_words[i];
				}

				TaggedWord[] next = aRightChunks[obj_idx_2 + 1].getWords();
	
				for (int i = pp_words.length; i < obj_words.length; i++) {
					obj_words[i] = next[i - pp_words.length];
				}

				objs = new Entity[1];
				objs[0] = new BaseEntity(obj_words);
				end_objs_idx = obj_idx_2 + 2;
			} else {
				objs = new Entity[1];
				objs[0] = new BaseEntity(aRightChunks[obj_idx_1].getWords());
				end_objs_idx = obj_idx_2 + 1;
			}
		} else if (aFrame == VerbFrame.NUM_24 || aFrame == VerbFrame.NUM_25) {
			/* frames 24 and 25 are ditransitive and take an NP and a
			   relation. */
			int obj_idx_1 = firstChunkOfType(aRightChunks, ChunkType.NP),
				obj_idx_2;

			if (obj_idx_1 == -1) {
				return new Relation[0];
			}

			obj_idx_2 = firstChunkOfType(aRightChunks, ChunkType.SBAR, 
				obj_idx_1 + 1);

			TaggedSentence.Chunk[] subord_clause = Arrays.asList(aRightChunks).
				subList(obj_idx_2 + 1, aRightChunks.length).
				toArray(new TaggedSentence.Chunk[0]);

			Relation[] subord_rels = extractFromClause(subord_clause);

			if (subord_rels.length == 0) {
				return new Relation[0];
			}

			objs = new Entity[2];
			objs[0] = new BaseEntity(aRightChunks[obj_idx_1].getWords());
			objs[1] = new RelationEntity(subord_rels[0]);
			end_objs_idx = aRightChunks.length;
		} else if (aFrame == VerbFrame.NUM_30) {
			/* frame 30 is, e.g., "talks Bob into eating a sandwich." */
			int obj_idx_1 = firstChunkOfType(aRightChunks, ChunkType.NP),
				obj_idx_2;

			if (obj_idx_1 == -1) {
				return new Relation[0];
			}

			for (obj_idx_2 = obj_idx_1 + 1; obj_idx_2 < aRightChunks.length;
					 obj_idx_2++) {
				if (aRightChunks[obj_idx_2].getType() == ChunkType.PP &&
					aRightChunks[obj_idx_2].getWords().length == 1 &&
					aRightChunks[obj_idx_2].getWords()[0].getWord().
						equalsIgnoreCase("into")) break;
			}

			if (obj_idx_2 >= aRightChunks.length - 1) {
				return new Relation[0];
			}

			TaggedSentence.Chunk[] subord_clause = Arrays.asList(aRightChunks).
				subList(obj_idx_2 + 1, aRightChunks.length).
				toArray(new TaggedSentence.Chunk[0]);

			Relation[] subord_rels = extractFromClause(subord_clause);

			if (subord_rels.length == 0) {
				return new Relation[0];
			}

			objs = new Entity[2];
			objs[0] = new BaseEntity(aRightChunks[obj_idx_1].getWords());
			objs[1] = new RelationEntity(subord_rels[0]);
			end_objs_idx = aRightChunks.length;
		}

		else {
			System.out.println("Unhandled verb frame " + aFrame);
		}

		Relation[] rels = new Relation[1];

		if (objs != null)
		for (Entity e : objs) {
			if (e == null || e.toSerialRep() == "") {
				System.out.println("object is null in verb frame " + aFrame);
				throw new OutOfMemoryError();
			}
		}

		rels[0] = new Relation(subj, aPred, objs, new Annotation[0]);

		return rels;
	}

	public Relation[] extract(TaggedSentence aSent) {
		if (!aSent.isChunked()) return null;

		return extractFromClause(aSent.getChunks());
	}
}
