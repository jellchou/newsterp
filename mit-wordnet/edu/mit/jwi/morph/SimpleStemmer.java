/******************************************************************************
 * Copyright (c) 2007 Mark Alan Finlayson
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT Java Wordnet Interface 
 * Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.mit.edu/~markaf/projects/wordnet/license.html.
 *****************************************************************************/

package edu.mit.jwi.morph;

import java.util.SortedSet;
import java.util.TreeSet;

import edu.mit.jwi.item.PartOfSpeech;

/**
 * Provides simple a simple pattern-based stemming facility based on the "Rules
 * of Detachment" as described in the morphy man page in the Wordnet
 * distribution, which can be found at <a
 * href="http://wordnet.princeton.edu/man/morphy.7WN.html">
 * http://wordnet.princeton.edu/man/morphy.7WN.html</a> It also attempts to
 * strip "ful" endings. It does not search Wordnet to see if stems actually
 * exist. In particular, quoting from that man page:
 * <p>
 * <h3>Rules of Detachment</h3>
 * <p>
 * The following table shows the rules of detachment used by Morphy. If a word
 * ends with one of the suffixes, it is stripped from the word and the
 * corresponding ending is added. ... No rules are applicable to adverbs.
 * <p>
 * POS Suffix Ending<br>
 * <ul>
 * <li>NOUN "s" ""
 * <li>NOUN "ses" "s"
 * <li>NOUN "xes" "x"
 * <li>NOUN "zes" "z"
 * <li>NOUN "ches" "ch"
 * <li>NOUN "shes" "sh"
 * <li>NOUN "men" "man"
 * <li>NOUN "ies" "y"
 * <li>VERB "s" ""
 * <li>VERB "ies" "y"
 * <li>VERB "es" "e"
 * <li>VERB "es" ""
 * <li>VERB "ed" "e"
 * <li>VERB "ed" ""
 * <li>VERB "ing" "e"
 * <li>VERB "ing" ""
 * <li>ADJ "er" ""
 * <li>ADJ "est" ""
 * <li>ADJ "er" "e"
 * <li>ADJ "est" "e"
 * </ul>
 * <p>
 * <h3>Special Processing for nouns ending with 'ful'</h3>
 * <p>
 * Morphy contains code that searches for nouns ending with ful and performs a
 * transformation on the substring preceeding it. It then appends 'ful' back
 * onto the resulting string and returns it. For example, if passed the nouns
 * boxesful, it will return boxful.
 * 
 * @author M.A. Finlayson
 * @version 1.0, 4/12/07
 * @since 1.5.0
 */
public class SimpleStemmer {

    public static final String SUFFIX_ches = "ches";
    public static final String SUFFIX_ed = "ed";
    public static final String SUFFIX_es = "es";
    public static final String SUFFIX_est = "est";
    public static final String SUFFIX_er = "er";
    public static final String SUFFIX_ful = "ful";
    public static final String SUFFIX_ies = "ies";
    public static final String SUFFIX_ing = "ing";
    public static final String SUFFIX_men = "men";
    public static final String SUFFIX_s = "s";
    public static final String SUFFIX_ses = "ses";
    public static final String SUFFIX_shes = "shes";
    public static final String SUFFIX_xes = "xes";
    public static final String SUFFIX_zes = "zes";

    public static final String ENDING_null = "";
    public static final String ENDING_ch = "ch";
    public static final String ENDING_e = "e";
    public static final String ENDING_man = "man";
    public static final String ENDING_s = SUFFIX_s;
    public static final String ENDING_sh = "sh";
    public static final String ENDING_x = "x";
    public static final String ENDING_y = "y";
    public static final String ENDING_z = "z";

    String[][] nounMappings = new String[][] {
            new String[] { SUFFIX_s, ENDING_null },
            new String[] { SUFFIX_ses, ENDING_s },
            new String[] { SUFFIX_xes, ENDING_x },
            new String[] { SUFFIX_zes, ENDING_z },
            new String[] { SUFFIX_ches, ENDING_ch },
            new String[] { SUFFIX_shes, ENDING_sh },
            new String[] { SUFFIX_men, ENDING_man },
            new String[] { SUFFIX_ies, ENDING_y }, };

    String[][] verbMappings = new String[][] {
            new String[] { SUFFIX_s, ENDING_null },
            new String[] { SUFFIX_ies, ENDING_y },
            new String[] { SUFFIX_es, ENDING_e },
            new String[] { SUFFIX_es, ENDING_null },
            new String[] { SUFFIX_ed, ENDING_e },
            new String[] { SUFFIX_ed, ENDING_null },
            new String[] { SUFFIX_ing, ENDING_e },
            new String[] { SUFFIX_ing, ENDING_null }, };

    String[][] adjMappings = new String[][] {
            new String[] { SUFFIX_er, ENDING_e },
            new String[] { SUFFIX_er, ENDING_null },
            new String[] { SUFFIX_est, ENDING_e },
            new String[] { SUFFIX_est, ENDING_null }, };

    /** Returns all possible roots for the specifed word.  The Set is
     * sorted in String order.
     */
    public SortedSet<String> getRoots(final String word) {

        SortedSet<String> result = null;
        SortedSet<String> roots;

        roots = stripNounSuffix(word);
        if (result == null & roots != null)
            result = new TreeSet<String>();
        if (roots != null)
            result.addAll(roots);

        roots = stripVerbSuffix(word);
        if (result == null & roots != null)
            result = new TreeSet<String>();
        if (roots != null)
            result.addAll(roots);

        roots = stripAdjectiveSuffix(word);
        if (result == null & roots != null)
            result = new TreeSet<String>();
        if (roots != null)
            result.addAll(roots);

        return result;

    }

    /** Returns a set of possible roots for the specifed word, considered
     * as being a member of the specified part of speech.  The Set is
     * sorted in String order.
     */
    public SortedSet<String> getRoots(String word, PartOfSpeech pos) {

        if (word == null)
            return null;
        if (pos == PartOfSpeech.ADVERB) {
            return null;
        } else if (pos == PartOfSpeech.NOUN) {
            return stripNounSuffix(word);
        } else if (pos == PartOfSpeech.VERB) {
            return stripVerbSuffix(word);
        } else if (pos == PartOfSpeech.ADJECTIVE) {
            return stripAdjectiveSuffix(word);
        }
        return null;
    }

    /** Internal method for stripping noun suffixes.
     */
    protected SortedSet<String> stripNounSuffix(final String noun) {
        if (noun == null)
            return null;

        int idx;
        String word = noun;
        boolean endsWithFUL = false;
        if (noun.endsWith(SUFFIX_ful)) {
            endsWithFUL = true;
            idx = word.lastIndexOf(SUFFIX_ful);
            word = noun.substring(0, idx);
        }

        SortedSet<String> result = null;
        StringBuffer stem;
        for (String[] mapping : nounMappings) {
            if (!word.endsWith(mapping[0]))
                continue;
            idx = word.lastIndexOf(mapping[0]);
            stem = new StringBuffer();
            for (int i = 0; i < idx; i++)
                stem.append(word.charAt(i));
            stem.append(mapping[1]);
            if (endsWithFUL)
                stem.append(SUFFIX_ful);
            if (result == null)
                result = new TreeSet<String>();
            result.add(stem.toString());
        }
        return result;
    }

    /** Internal method for stripping verb suffixes.
     */
    protected SortedSet<String> stripVerbSuffix(final String word) {
        if (word == null)
            return null;
        SortedSet<String> result = null;
        int idx;
        StringBuffer stem;
        for (String[] mapping : verbMappings) {
            if (!word.endsWith(mapping[0]))
                continue;
            idx = word.lastIndexOf(mapping[0]);
            stem = new StringBuffer();
            for (int i = 0; i < idx; i++)
                stem.append(word.charAt(i));
            stem.append(mapping[1]);
            if (result == null)
                result = new TreeSet<String>();
            result.add(stem.toString());
        }
        return result;
    }

    /** Internal method for stripping adjective suffixes.
     */
    protected SortedSet<String> stripAdjectiveSuffix(final String word) {
        if (word == null)
            return null;
        SortedSet<String> result = null;
        int idx;
        StringBuffer stem;
        for (String[] mapping : adjMappings) {
            if (!word.endsWith(mapping[0]))
                continue;
            idx = word.lastIndexOf(mapping[0]);
            stem = new StringBuffer();
            for (int i = 0; i < idx; i++)
                stem.append(word.charAt(i));
            stem.append(mapping[1]);
            if (result == null)
                result = new TreeSet<String>();
            result.add(stem.toString());
        }
        return result;
    }

}
