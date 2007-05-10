#! /usr/bin/python2.4
#
#

import math
import util

""" This class will classify inputs according to the
bayes learner model output from bayes-learner. """

__author__ = 'jhebert@cs.washington.edu (Jack Hebert)'



class BayesClassifier:
    def __init__(self):
        self.features = {}

    def LoadModel(self, fileName):
        data = open(fileName).read().split('\n')
        for line in data:
            try:
                feature, score1, score2 = line.split('\t')
                score1, score2 = int(score1), int(score2)
                total = float(score1 + score2)
                log1, log2 = math.log(score1 / total), math.log(score2 / total)
                self.features[feature] = [log1, log2]
            except ValueError:
                print 'Failed to load:', line
                continue
            except OverflowError:
                print 'Score1: ', score1
                print 'Score2: ', score2
                print 'total: ', total

    def ClassifyDoc(self, doc):
        val1, val2 = 0.0, 0.0
        for word in doc:
            if(word in self.features):
                val1 += self.features[word][0]
                val2 += self.features[word][1]
            else:
                #print 'Missing:', word
                pass
        if(val1 > val2):
            return 0
        else:
            return 1

def test(bayesClassifier):
    path = '/home/jhebert/code/java/newsterp/articles/univ-bridge'
    items = util.LoadAndSplitFromDir(path)
    for item in items:
        print bayesClassifier.ClassifyDoc(item)



def main():
    b = BayesClassifier()
    b.LoadModel('model.test')
    test(b)

#main()
