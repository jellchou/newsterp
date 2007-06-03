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
        self.blacklist = {'http://news.yahoo.com/i/1760':None,
                          'http://news.yahoo.com/i/964':None,
                          'http://search.yahoo.com/mrss':None}
                          

    def run(self):
        rel = self.relationReader.ReadNextRelation()
        print 'Extracting relations and stemming...'
        while(rel != None):
            self.relationCount = self.relationCount+1
            self.SaveRelation(rel)
            url = rel.articleURL
            if(url in self.blacklist):
                rel = self.relationReader.ReadNextRelation()
                continue
            r1, r2, r3, r4 = self.GenerateRelations(rel)
            self.relations[self.relationCount] = (self.relationCount, r2, rel)
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
        items = [self.relationCount,rel.RelationAsText(),
                 rel.articleURL]
        items = [str(a) for a in items]
        self.relationFile.write(' : '.join(items))
        self.relationFile.write('\n')

    def FindCollisions(self):
        toConsider, matchCounts = {}, {}
        print 'Finding collisions...'
        for word in self.index:
            #print 'Word:', word
            items = self.index[word]
            #print 'Items:', items
            for i in range(len(items)):
                for j in range(len(items)):
                    if(i==j):
                        continue
                    r1 = self.relations[items[i]][:2]
                    r2 = self.relations[items[j]][:2]
                    if(not (r1 in toConsider)):
                        toConsider[r1] = []
                    toConsider[r1].append(r2)

                    u1 = self.relations[items[i]][2].articleURL
                    u2 = self.relations[items[j]][2].articleURL
                    if(u1==u2):
                        continue
                    if(not (u1 in matchCounts)):
                        matchCounts[u1]=[]
                    matchCounts[u1].append(u2)
                    if(not (u2 in matchCounts)):
                        matchCounts[u2]=[]
                    matchCounts[u2].append(u1)
                    
        print 'Cleaning them up...'
        for item in toConsider:
            set, hits = {}, toConsider[item]
            for h in hits:
                set[h]=None
            toConsider[item]=set.keys()
        print 'Writing them out...'
        toWrite = []
        for item in toConsider:
            toWrite.append(str(item)+'\n')
            toConsider[item].sort()
            for a in toConsider[item]:
                toWrite.append('\t'+str(a)+'\n')
        f = open('out-collisions.dat', 'w')
        f.write(''.join(toWrite))
        f.close()


        # count up the matches in match counts per item, reverse sort
        # print them out too.
        bigSort = []
        for url in matchCounts:
            unique = {}
            for item in matchCounts[url]:
                if(not item in unique):
                    unique[item] = 0
                unique[item] += 1
            ranked = []
            for item in unique:
                ranked.append((unique[item], item))
            ranked.sort()
            ranked.reverse()
            score = ranked[0][0]

            items = [url, ':',
                     ''.join(['\n\t'+str(a) for a in ranked[:15]])]
            bigSort.append((score, ' '.join(items)))
        bigSort.sort()
        #bigSort.reverse()
        print '\n'.join([str(a[1]) for a in bigSort])



        

def main():
    c = Colluder()
    c.Init()
    c.run()
    #c.PrintIndex()
    c.SaveIndex()
    c.FindCollisions()


if(__name__=='__main__'):
    main()

