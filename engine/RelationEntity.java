/*
 * NewsTerp Engine - We report.  You decipher.
 * copyright (c) 2007 Colin Bayer, Jack Hebert
 *
 * CSE 472 Spring 2007 final project
 */

public class RelationEntity implements Entity {
	public RelationEntity(Relation aRelation) {
		mRelation = aRelation;
	}

	public String toString() {
		return mRelation.toString();
	}

	public String toSerialRep() {
		return mRelation.toSerialRep();
	}

	public Entity resolve(Entity[] aResolutionContext) {
		return null;
	}

	private Relation mRelation;
}
