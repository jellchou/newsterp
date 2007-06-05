/*
 * NewsTerp Engine - We report.  You decipher.
 * copyright (c) 2007 Colin Bayer, Jack Hebert
 *
 * CSE 472 Spring 2007 final project
 */

import java.util.ArrayList;
import java.util.Arrays;

public class Phase1RelationExtractor implements RelationExtractor {
	public Phase1RelationExtractor() {}

	Relation[] extractFromClause(TaggedSentence.Chunk[] aChunks) {
		ArrayList<Relation> main_rels = new ArrayList<Relation>();
		TaggedSentence.Chunk subj = null, pred = null, obj = null;

		for (int i = 0; i < aChunks.length; i++) {
			if (aChunks[i].getType() == ChunkType.NP) {
				/* NP; if it's the first one in this clause, it's a subject. */
				if (subj == null) subj = aChunks[i];

				/* if there's already been an NP and a VP, then this is the
				   object. */
				else if (pred != null) {
					obj = aChunks[i];
				}	
			} else if (aChunks[i].getType() == ChunkType.VP) {
				/* VP; if it's the first one in this clause, it's the clause's
				   predicate. */
				if (pred == null) pred = aChunks[i];
			} else if (aChunks[i].getType() == ChunkType.SBAR) {
				/* S-bar node; beginning of a subordinate clause.  extract from
				   all words/chunks to the right of this one, subordinate
				   relation is the object of the main relation. */
				TaggedSentence.Chunk[] subord_chunks =
					Arrays.asList(aChunks).subList(i + 1, aChunks.length)
						.toArray(new TaggedSentence.Chunk[0]);

				/* this is an array of subordinate relations, in the weird case
				   of coordinated subordinate clauses ("the president said that
				   Brian was at fault and similar mistakes would not occur in
				   the future.") */
				Relation[] subord_rels = extractFromClause(subord_chunks);

				if (subj == null || pred == null) {
					/* weird sentence structure, maybe like "That you would
					   say that surprises me."; treat the subordinate relations
					   as main relations */
					main_rels.addAll(Arrays.asList(subord_rels));
				} else {
					for (Relation subord_rel : subord_rels) {
						Entity[] objs;

						if (obj != null) {
							objs = new Entity[1];
							objs[0] = new RelationEntity(subord_rel);
						} else {
							objs = new Entity[0];
						}

						main_rels.add(new Relation(
							new BaseEntity(subj.getWords()),
							new Predicate(pred.getWords()),
							objs, new Annotation[0])); 
					}
				}

				/* assume that subordinate clauses consume the remainder of the
				   sentence, and return what we have. */
				subj = null;
				pred = null;
				obj = null;
				break;
			} else if (aChunks[i].getType() == ChunkType.CONJP) {
				/* conjunction phrase; if there's a full sentence (subject and
				   predicate) already, then assume that the conjoined objects
				   are entire sentences. */
				if (subj != null && obj != null) {
					Entity[] objs;

					if (obj == null) {
						objs = new Entity[0];
					} else {
						objs = new Entity[1];
						objs[0] = new BaseEntity(obj.getWords());
					}

					main_rels.add(new Relation(
						new BaseEntity(subj.getWords()),
						new Predicate(pred.getWords()),
						objs, new Annotation[0]));
				}

				subj = null; pred = null; obj = null;
			}
		}

		/* if a relation ran to the end, dump it into main_rels. */
		if (subj != null && pred != null) {
			Entity[] objs;

			if (obj != null) {
				objs = new Entity[1];
				objs[0] = new BaseEntity(obj.getWords());
			} else {
				objs = new Entity[0];
			}

			main_rels.add(new Relation(
				new BaseEntity(subj.getWords()),
				new Predicate(pred.getWords()),
				objs, new Annotation[0]));
		}

		return main_rels.toArray(new Relation[0]);
	}

	public Relation[] extract(TaggedSentence aSent) {
		if (!aSent.isChunked()) return null;

		return extractFromClause(aSent.getChunks());
	}
}
