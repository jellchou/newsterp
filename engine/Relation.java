/*
 * NewsTerp Engine - We report.  You decipher.
 * copyright (c) 2007 Colin Bayer, Jack Hebert
 *
 * CSE 472 Spring 2007 final project
 */

import java.util.Arrays;
import java.util.LinkedList;

public class Relation {
	public Relation() {
		mSubject = null;
		mObjects = new Entity[0];
		mPredicate = null;
		mAnnotations = new LinkedList<Annotation>();
	}

	public Relation(Entity aSubject, Predicate aPredicate, Entity[] aObjects,
			Annotation[] aAnnotations) {
		mSubject = aSubject;
		mObjects = aObjects;
		mPredicate = aPredicate;
		mAnnotations = new LinkedList<Annotation>(Arrays.asList(aAnnotations));
	}

	public void annotate(Annotation aAnno) {
		mAnnotations.add(aAnno);
	}

	private String objsToSerialRep() {
		String rv = "";

		if (mObjects == null || mObjects.length == 0) return "";

		for (Entity obj : mObjects) {
			rv += "," + obj.toSerialRep();
		}

		return rv.substring(1);
	}

	private String annosToSerialRep() {
		String rv = "";

		if (mAnnotations == null || mAnnotations.size() == 0) return "";

		for (Annotation anno : mAnnotations) {
			rv += "," + anno.toSerialRep();
		}

		return rv.substring(1);
	}

	public String toString() {
		if (mSubject == null || mPredicate == null) return "(null relation)";

		return mSubject + "." + mPredicate + "(" + objsToSerialRep() + 
			")";
	}

	public String toSerialRep() {
		if (mSubject == null || mPredicate == null) return ";\n";
		return "(" + mSubject.toSerialRep() + "," + mPredicate.toSerialRep() + 
			"," + objsToSerialRep() + ")+[" + annosToSerialRep() + "]";
	}

    private Entity mSubject;
	private Entity[] mObjects;
	private LinkedList<Annotation> mAnnotations;

    private Predicate mPredicate;	        
}
