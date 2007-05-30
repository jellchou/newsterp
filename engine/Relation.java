/*
 * NewsTerp Engine - We report.  You decipher.
 * copyright (c) 2007 Colin Bayer, Jack Hebert
 *
 * CSE 472 Spring 2007 final project
 */

import java.util.Arrays;

public class Relation {
	public Relation() {
		mSubject = mObject = null;
		mPredicate = null;
	}

	public Relation(Entity aSubject, Predicate aPredicate, Entity aObject,
			Entity[] aAnnotations) {
		mSubject = aSubject;
		mObject = aObject;
		mPredicate = aPredicate;
		mAnnotations = aAnnotations;
	}

	public String toString() {
		if (mSubject == null || mPredicate == null) return "(null relation)";

		return mSubject + "." + mPredicate + "(" + ((mObject != null) ? mObject : "") + ")";
	}

	public String toSerialRep() {
		if (mSubject == null || mPredicate == null) return ";\n";
		return "(" + mSubject + "," + mPredicate + "," + mObject + ")+" +
			Arrays.toString(mAnnotations) + ";\n";
	}

    private Entity mSubject, mObject;
	private Entity[] mAnnotations;

    private Predicate mPredicate;	        
}
