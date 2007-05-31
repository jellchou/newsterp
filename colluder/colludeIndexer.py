#! /usr/bin/python2.4


__author__ = 'jhebert@cs.washington.edu (Jack Hebert)'

import stopWorder
import porterStemmer
import relation
from relationReader import RelationReader

class Colluder:
    def __init__(self):
        self.stopWorder = stopWorder.StopWorder()
        self.stemmer = porterStemmer.PorterStemmer()
        #self.inputFileName = '../engine/relations.dat'
        self.inputFileName = 'relations2.dat'
        self.relationReader = RelationReader()
        self.relationCount = 0
        self.relations = {}
        self.index = {}
        self.relationFile = open('out-relationCountIndex.dat', 'w')

    def Init(self):
        self.stopWorder.Init()
        self.relationReader.Open(self.inputFileName)

    def run(self):
        rel = self.relationReader.ReadNextRelation()
        print 'Extracting relations and stemming...'
        while(rel != None):
            self.relationCount = self.relationCount+1
            self.SaveRelation(rel)
            r1, r2, r3, r4 = self.GenerateRelations(rel)
            #for r in [r1, r2, r3, r4]:
            for r in [r4]:
                for word in r.split():
                    if(not(word in self.index)):
                        self.index[word]=[self.relationCount]
                    currList = self.index[word]
                    if(currList[-1]!=self.relationCount):
                        currList.append(self.relationCount)
            rel = self.relationReader.ReadNextRelation()


    def GenerateRelations(self, relation):
        r1 = relation.RelationAsText()
        #print 'R1:', r1
        r2 = self.RemoveStopWords(r1)
        #print 'R2:', r2
        r3 = self.StemWords(r1)
        #print 'R3:', r3
        r4 = self.StemWords(r2)
        #print 'R4:', r4
        return [r1, r2, r3, r4]

    def RemoveStopWords(self, relation):
        toReturn = self.stopWorder.CleanText(relation)
        return toReturn

    def StemWords(self, relation):
        toReturn = self.stemmer.stem(relation,0,len(relation)-1)
        return toReturn

    def PrintIndex(self):
        for word in self.index:
            print word, ' : ', self.index[word]
        
    def SaveIndex(self):
        toWrite = []
        for word in self.index:
            items = [word, ' : ']+ self.index[word]
            line = ' '.join([str(a) for a in items])
            toWrite.append(line)
        f = open('out-wordToRelation.dat', 'w')
        f.write('\n'.join(toWrite))
        f.close()

    def SaveRelation(self, rel):
        self.relations[self.relationCount] = rel.RelationAsText()
        items = [self.relationCount,rel.RelationAsText(),
                 rel.articleURL]
        items = [str(a) for a in items]
        self.relationFile.write(' : '.join(items))
        self.relationFile.write('\n')

    def FindCollisions(self):
        toConsider = {}
        print 'Finding collisions...'
        for word in self.index:
            #print 'Word:', word
            items = self.index[word]
            #print 'Items:', items
            for i in range(len(items)):
                for j in range(len(items)):
                    if(i==j):
                        continue
                    if(not (items[i] in toConsider)):
                        toConsider[items[i]] = []
                    toConsider[items[i]].append(items[j])
                    #print 'Collision:', items[i], items[j]
        print 'Cleaning them up...'
        for item in toConsider:
            set, hits = {}, toConsider[item]
            for h in hits:
                set[h]=None
            toConsider[item]=set.keys()
        print 'Writing them out...'
        toWrite = []
        for item in toConsider:
            line = [item]+toConsider[item]
            toWrite.append(' : '.join([str(a) for a in line]))
        f = open('out-collisions.dat', 'w')
        f.write('\n'.join(toWrite))
        f.close()


def main():
    c = Colluder()
    c.Init()
    c.run()
    #c.PrintIndex()
    c.SaveIndex()
    c.FindCollisions()


if(__name__=='__main__'):
    main()

