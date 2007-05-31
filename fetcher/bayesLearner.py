#! /usr/bin/python2.4
#
#

import os
import util

""" This class will learn a bayes classifier given a
set of examples. It will be defined as working over
textual corpora. """

__author__ = 'jhebert@cs.washington.edu (Jack Hebert)'


# TODO: don't I need to track the number of positive and
#       negative documents in total to normalize?

class BayesLearner:

    def __init__(self, typeName):
        self.features = {}
        self.posPath = ''
        self.negPath = ''
        self.StopList = util.StopList()
        self.type = typeName
        self.totalCount = [1, 1]

    def AddExample(self, doc, val):
        self.totalCount[val] += 1
        for word in doc:
            self.StopList.AddWord(word)
            if(not (word in self.features)):
                self.features[word] = [1, 1]
            self.features[word][val] += 1

    def LearnModel(self):
        print 'Learning Model ...'
        positive = self.LoadExamples(self.posPath)
        negative = self.LoadExamples(self.negPath)
        print 'Done loading data.'
        print 'Learning positive...'
        for doc in positive:
            #print 'Doc:', doc
            self.AddExample(doc, 1)
        print 'Learning negative...'
        for doc in negative:
            self.AddExample(doc, 0)
    
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
            toReturn = []
            lines = open(path).read().split('\n')
            for line in lines:
                toAdd = util.SplitToWords(line)
                #print 'Adding:', toAdd
                toReturn.append(toAdd)
            return toReturn
        else:
            return open(path).read().split('\n')
    


def main():
    b = BayesLearner('txt')
    #b = BayesLearner('news')
    b.posPath = './models/pos-news.txt'
    #b.negPath = './models/neg-news.txt'
    b.negPath = './models/neg-text.txt'

    b.LearnModel()
    print b.StopList.GetTopN(60)
    b.SaveModel('text.model')
    #print b.features


main()





