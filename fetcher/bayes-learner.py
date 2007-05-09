#! /usr/bin/python2.4
#
#

import os
import util

""" This class will learn a bayes classifier given a
set of examples. It will be defined as working over
textual corpora. """

__author__ = 'jhebert@cs.washington.edu (Jack Hebert)'




class BayesLearner:

    def __init__(self):
        self.features = {}
        self.posPath = ''
        self.negPath = ''
        self.StopList = util.StopList()

    def AddExample(self, doc, val):
        for word in doc:
            self.StopList.AddWord(word)
            if(not (word in self.features)):
                self.features[word] = [1, 1]
            self.features[word][val] += 1

    def LearnModel(self):
        positive = self.LoadExamples(self.posPath)
        negative = self.LoadExamples(self.negPath)
        for doc in positive:
            self.AddExample(doc, 1)
        for doc in negative:
            self.AddExample(doc, 0)
    
    def SaveModel(self, fileName):
        toWrite, f = [], open(fileName, 'w')
        for feature in self.features:
            line = '\t'.join([feature]+[str(a) for a in self.features[feature]])
            toWrite.append(line)
        f.write('\n'.join(toWrite))
        f.close()

    def LoadExamples(self, dir):
        return util.LoadAndSplitFromDir(dir)
    


def main():
    b = BayesLearner()
    b.posPath = '/home/jhebert/code/java/newsterp/articles/univ-bridge'
    b.negPath = '/home/jhebert/code/java/newsterp/fetcher/'
    b.LearnModel()
    print b.StopList.GetTopN(60)
    b.SaveModel('model.test')
    #print b.features


main()





