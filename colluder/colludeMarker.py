#! /usr/bin/python2.4


__author__ = 'jhebert@cs.washington.edu (Jack Hebert)'

import relation
import stopWorder
from relationReader import RelationReader



class Marker:
    def __init__(self):
        self.inputFileName = 'relations3.dat'
        self.relationReader = RelationReader()
        self.stopWorder = stopWorder.StopWorder()
        self.articleMatches = {}
        self.articleToRelation = {}
        self.magicMatchNumber = 2

    def Init(self):
        self.stopWorder.Init()
        self.relationReader.Open(self.inputFileName)

    def run(self):
        self.GetRelationMatches()
        self.GetArticleToRelations()
        self.MarkAllGoodRelations()

    def GetRelationMatches(self):
        data=open('out-articleMatches.dat').read().split('\n')
        for line in data:
            if(len(line)<3):
                continue
            items = line.split('\t')
            key, rest = items[0], items[1:]
            self.articleMatches[key] = rest

    def GetArticleToRelations(self):
        rel = self.relationReader.ReadNextRelation()
        while(rel != None):
            url = rel.articleURL
            relationText = rel.RelationAsText().lower() # stopword?
            relationText = self.stopWorder.CleanText(relationText)
            if(not (url in self.articleToRelation)):
                self.articleToRelation[url] = []
            self.articleToRelation[url].append(relationText)
            rel = self.relationReader.ReadNextRelation()

    def MarkAllGoodRelations(self):
        for article in self.articleMatches:
            good=self.GetImportantRelations(article)
            print 'Article:', article
            print '\n'.join(['\t'+str(a) for a in good])
            print '\n'*3

    def GetImportantRelations(self, article):
        """ This needs to mark which relations overlap most. """
        hits = {}
        # Loops should probably be structure the other way.
        for r1 in self.articleToRelation[article]:
            for art in self.articleMatches[article]:
                for r2 in self.articleToRelation[art]:
                    if(self.GoodMatch(r1, r2)):
                        if(not(r1 in hits)):
                            hits[r1] = 0
                        hits[r1] += 1
        toSort = [(hits[a], a) for a in hits]
        toSort.sort()
        toSort.reverse()
        return [str(a) for a in toSort]


    def GoodMatch(self, r1, r2):
        words, count = {}, 0
        for word in r1.split():
            words[word] = None
        for word in r2.split():
            if(word in words):
                count += 1
        return (count > self.magicMatchNumber)


def main():
    m = Marker()
    m.Init()
    m.run()


if(__name__ == '__main__'):
    main()
