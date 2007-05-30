/*
 * NewsTerp Engine - We report.  You decipher.
 * copyright (c) 2007 Colin Bayer, Jack Hebert
 *
 * CSE 472 Spring 2007 final project
 */

public class BaselineRelationExtractor implements RelationExtractor {
	public BaselineRelationExtractor() {}

	public Relation[] extract(TaggedSentence aSent) {
		Relation[] rv = new Relation[1];

		TaggedSentence.Chunk[] nps, vps;

		if (!aSent.isChunked()) return null;

		nps = aSent.getChunks(ChunkType.NP);
		vps = aSent.getChunks(ChunkType.VP);

		if (nps.length == 0 || vps.length == 0) return null;

		rv[0] = new Relation(
			new BaseEntity(nps[0].getWords()), 
			new Predicate(vps[0].getWords()),
			nps.length == 1 ? null : new BaseEntity(nps[1].getWords()),
			new Entity[0]);

		return rv;
	}
}
