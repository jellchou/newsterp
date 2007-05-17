/*
 * NewsTerp Engine - We report.  You decipher.
 * copyright (c) 2007 Colin Bayer, Jack Hebert
 *
 * CSE 472 Spring 2007 final project
 */

public class RelationExtractor {
	public RelationExtractor() {}

	public Relation extract(TaggedSentence aSent) {
		TaggedSentence.Chunk[] nps, vps;

		if (!aSent.isChunked()) return null;

		nps = aSent.getChunks(ChunkType.NP);
		vps = aSent.getChunks(ChunkType.VP);

		if (nps.length == 0 || vps.length == 0) return null;

		return new Relation(nps[0], vps[0], nps.length == 1 ? null : nps[1]);
	}
}
