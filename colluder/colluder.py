#! /usr/bin/python2.4


__author__ = 'jhebert@cs.washington.edu (Jack Hebert)'

import stopWorder
import porterStemmer


class Colluder:
    def __init__(self):
        self.stopWorder = stopWorder.StopWorder()
        self.stemmer = porterStemmer.PorterStemmer()
        self.inputFileName = ''

    def Init(self):
        self.stopWorder.Init()


    def run(self):
        data = open(self.inputFileName).read().split('\n')
        for relation in data:
            relation1 = relation
            relation2 = self.RemoveStopWords(relation)
            relation3 = self.StemWords(relation)
            relation4 = self.StemWords(relation2)
            

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

