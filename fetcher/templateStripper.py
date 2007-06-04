#! /usr/bin/python2.4


__author__ = 'jhebert@cs.washington.edu (Jack Hebert)'


import os
import time

class TemplateStripper:
    def __init__(self):
        self.grams = {}
        self.docs = []
        self.nLength = 3
        self.done = {}

    def IsADup(self, doc):
        doc = doc.lower()
        doc = doc.strip()
        index = doc.find(' ')
        doc = doc[index+1:]
        doc = doc.replace(' ', '')
        doc = doc.replace('\t','')
        doc = doc.replace('\n','')
        doc = doc[:300]
        h = hash(doc)
        if(h in self.done):
            return True
        self.done[h] = None
        return False

    def AddDoc(self, doc):
        if(self.IsADup(doc)):
            return
        self.docs.append(doc)
        words,window = doc.split(), []
        for word in words:
            window.append(word)
            gram = ' '.join(window)
            if(not (gram in self.grams)):
                self.grams[gram] = 0
            self.grams[gram] += 1
            window=window[-(self.nLength-1):]
        while(len(window)>0):
            gram = ' '.join(window)
            if(not (gram in self.grams)):
                self.grams[gram] = 0
            self.grams[gram] += 1
            window=window[1:]


    def OutputDocs(self):
        toReturn=[]
        for doc in self.docs:
            words,window = doc.split(),[]
            skipCount,currArticle=0,[]    
            for word in words:
                window.append(word)
                gram = ' '.join(window)
                if(self.grams[gram]*2>len(self.docs)):
                    skipCount=self.nLength
                    #print 'Skipping:',window[0]
                elif(skipCount==0):
                    if(len(window)==self.nLength):
                        currArticle.append(window[0])
                else:
                    #print 'Skipping:',window[0]
                    skipCount-=1
                window=window[-(self.nLength-1):]
            while(len(window)>0):
                gram = ' '.join(window)
                if(self.grams[gram]*3>len(self.docs)):
                    skipCount=len(window)
                    #print 'Skipping:',window[0]
                elif(skipCount==0):
                    currArticle.append(window[0])
                else:
                    #print 'Skipping:',window[0]
                    skipCount-=1
                window=window[1:]
            toReturn.append(' '.join(currArticle))
        return toReturn
 

    def PrintStripGrams(self):
        for gram in self.grams:
            count=self.grams[gram]
            if(count*2>len(self.docs)):
                print count,len(self.docs),gram


    def ClearModel(self):
        self.docs = []
        self.grams = {}


def main():
    #dirPath = '../fetched-pages/golden/'
    dirPath = '../fetched-pages/pyrite/'
    files = os.listdir(dirPath)
    for f in files:
        if(f.find('.')>-1):
            continue
        if(f.find('-')>-1):
            continue
        if(f.find('~')>-1):
            continue
        print 'Stripping:', f
        t=TemplateStripper()        
        data=''
        try:
            data=open(dirPath+f).read()
        except:
            continue
        if(len(data)>0):
            for line in data.split('\n'):
                t.AddDoc(line)
        f=open(dirPath+f+'-stripped','w')
        f.write('\n'.join(t.OutputDocs()))
        f.close()





if(__name__=='__main__'):
    main()
