#! /usr/bin/python2.4
#
#

import os
import util

""" This class will learn a bayes classifier given a
set of examples. It will be defined as working over
textual corpora. """

__author__ = 'jhebert@cs.washington.edu (Jack Hebert)'


# TODO: move this file to bayesLearner.py

class BayesLearner:

    def __init__(self):
        self.features = {}
        self.posPath = ''
        self.negPath = ''
        self.StopList = util.StopList()
        self.type = 'txt'

    def AddExample(self, doc, val):
        for word in doc:
            self.StopList.AddWord(word)
            if(not (word in self.features)):
                self.features[word] = [1, 1]
            self.features[word][val] += 1

    def LearnModel(self):
        print 'Learning Model ...'
        positive = self.LoadExamples(self.posPath)
        negative = self.LoadExamples(self.negPath)
        for doc in positive:
            for token in doc:
                self.AddExample(token, 1)
        for doc in negative:
            for token in doc:
                self.AddExample(token, 0)
    
    def SaveModel(self, fileName):
        print 'Saving model to: ', fileName
        toWrite, f = [], open(fileName, 'w')
        for feature in self.features:
            line = '\t'.join([feature]+[str(a) for a in self.features[feature]])
            toWrite.append(line)
        f.write('\n'.join(toWrite))
        f.close()

    def LoadExamples(self, path):
        print 'Loading data from: ', path
        if(self.type == 'news'):
            return util.LoadAndSplitFromDir(path)
        else:
            return open(path).read()
    


def main():
    b = BayesLearner()
    #b.posPath = '/home/jhebert/code/java/newsterp/articles/univ-bridge'
    #b.negPath = '/home/jhebert/code/java/newsterp/fetcher/'
    b.posPath = './models/pos-text.txt'
    b.negPath = './models/neg-text.txt'

    b.LearnModel()
    print b.StopList.GetTopN(60)
    b.SaveModel('txt-vs-bin.model')
    #print b.features


main()





