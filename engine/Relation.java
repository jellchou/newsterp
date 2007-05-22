/*
 * NewsTerp Engine - We report.  You decipher.
 * copyright (c) 2007 Colin Bayer, Jack Hebert
 *
 * CSE 472 Spring 2007 final project
 */

public class Relation {
	public Relation() {
		mSubject = mObject = null;
		mPredicate = null;
	}

	public Relation(Entity aSubject, Predicate aPredicate, Entity aObject) {
		mSubject = aSubject;
		mObject = aObject;
		mPredicate = aPredicate;
	}

	public String toString() {
		if (mSubject == null || mPredicate == null) return "(null relation)";

		return mSubject + "." + mPredicate + "(" + ((mObject != null) ? mObject : "") + ")";
	}


    private Entity mSubject, mObject;
    private Predicate mPredicate;	        
}
