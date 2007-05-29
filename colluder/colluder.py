#! /usr/bin/python2.4


__author__ = 'jhebert@cs.washington.edu (Jack Hebert)'

import stopWorder
import porterStemmer


class Colluder:
    def __init__(self):
        self.stopWorder = stopWorder.StopWorder()
        self.stemmer = porterStemmer.PorterStemmer()
        self.inputFileName = ''
        self.relationCount = 0
        self.index = {}

    def Init(self):
        self.stopWorder.Init()


    def run(self):
        data = open(self.inputFileName).read().split('\n')
        for relation in data:
            self.relationCount = self.relationCount+1
            r1 = relation
            r2 = self.RemoveStopWords(relation)
            r3 = self.StemWords(relation)
            r4 = self.StemWords(relation2)
            for r in [r1, r2, r3, r4]:
                for word in r:
                    if(not(word in self.index)):
                        self.index[word]=[]
                    currList = self.index[word]
                    if(currList[-1]!=self.relationCount):
                        currList.append(self.relationCount)
        # TODO: output index to file.

    def GenerateRelations(self, relation):
        toReturn = []
        return toReturn

    def RemoveStopWords(self, relation):
        toReturn = self.stopWorder.CleanText(relation)
        return toReturn

    def StemWords(self, relation):
        toReturn = self.stemmer.stem(relation, 0, len(relation))
        return toReturn







def main():
    pass


if(__name__=='__main__'):
    main()

