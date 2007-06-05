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
        #self.inputFileName = 'relations2.dat'
        #self.inputFileName = 'relations3.dat'
        self.inputFileName = 'eval-relations-phase2.dat'
        #self.inputFileName = '../ok-relations-phase1.dat'
        self.relationReader = RelationReader()
        self.relationCount = 0
        self.relations = {}
        self.index = {}
        self.relationFile = open('out-relationCountIndex.dat', 'w')
        self.magicMatchNumber = 1

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
            if((url in self.blacklist)|(rel.articleURL=='')):
                rel = self.relationReader.ReadNextRelation()
                continue
            r1, r2, r3, r4 = self.GenerateRelations(rel)
            rToUse = r4
            self.relations[self.relationCount] = (self.relationCount, rToUse, rel)
            #for r in [r1, r2, r3, r4]:
            for r in [rToUse]:
                for word in r.split():
                    if(not(word in self.index)):
                        self.index[word]=[self.relationCount]
                    currList = self.index[word]
                    if(currList[-1]!=self.relationCount):
                        currList.append(self.relationCount)
            rel = self.relationReader.ReadNextRelation()


    def GenerateRelations(self, relation):
        r1 = relation.RelationAsText().lower()
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

    def GoodMatch(self, r1, r2):
        words, count = {}, 0
        for word in r1.split():
            words[word] = None
        for word in r2.split():
            if(word in words):
                count+= 1
        return (count > self.magicMatchNumber)

    # TODO: break down this god-awful method.
    def FindCollisions(self):
        toConsider, matchCounts = {}, {}
        print 'Finding collisions...'
        print 'Keeping relation-matchs with at least', self.magicMatchNumber, 'words in common.'
        count, oldPercent = 0, None
        for word in self.index:
            count +=1
            percent=int(100*float(count) / len(self.index))
            if(percent != oldPercent):
                print str(percent)+'% done.'
                oldPercent = percent
            #print 'Word:', word
            if(len(word)<2):
                continue
            items = self.index[word]
            for i in range(len(items)):
                for j in range(len(items)):
                    if(i==j):
                        continue
                    r1 = self.relations[items[i]][:2]
                    r2 = self.relations[items[j]][:2]
                    # if these don't really match, just continue.
                    if(not self.GoodMatch(r1[1], r2[1])):
                        continue

                    
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
        bigSort, toWrite = [], []
        for url in matchCounts:
            unique = {}
            for item in matchCounts[url]:
                if((item.find(url)>-1)|(url.find(item)>-1)):
                    continue
                if(not item in unique):
                    unique[item] = 0
                unique[item] += 1
            ranked = []
            for item in unique:
                ranked.append((unique[item], item))
            if(len(ranked)==0):
                continue
            ranked.sort()
            ranked.reverse()
            score = ranked[0][0]

            # Filter out aritcles with more that 150 matches!
            ranked = [a for a in ranked if a[0]<150]

            items = ['key:', url,
                     ''.join(['\n\t'+str(a[0])+': '+str(a[1]) for a in ranked])]
            toWrite.append('\t'.join([url]+[str(a[1]) for a in ranked if a[0]>1]))
            bigSort.append((score, ' '.join(items)))
        bigSort.sort()
        bigSort.reverse()
        #print '\n\n\n\n'.join([str(a[1]) for a in bigSort])
        f = open('out-articleMatches.dat', 'w')
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

