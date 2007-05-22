#! /usr/bin/python2.4


__author__ = 'jhebert@cs.washington.edu (Jack Hebert)'


import os
import time

class TemplateStripper:
    def __init__(self):
        self.grams = {}
        self.docs = []
        self.nLength = 3

    def AddDoc(self, doc):
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
            skipCount=0
            for word in words:
                window.append(word)
                gram = ' '.join(window)
                if(self.grams[gram]*2>len(self.docs)):
                    skipCount=self.nLength
                    print 'Skipping:',window[0]
                elif(skipCount==0):
                    if(len(window)==self.nLength):
                        toReturn.append(window[0])
                else:
                    print 'Skipping:',window[0]
                    skipCount-=1
                window=window[-(self.nLength-1):]
            while(len(window)>0):
                gram = ' '.join(window)
                if(self.grams[gram]*2>len(self.docs)):
                    skipCount=len(window)
                    print 'Skipping:',window[0]
                elif(skipCount==0):
                    toReturn.append(window[0])
                else:
                    print 'Skipping:',window[0]
                    skipCount-=1
                window=window[1:]
            toReturn.append('\n')
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
    t=TemplateStripper()
    files=os.listdir('../fetched-pages/test/')
    for f in files:
        data=''
        try:
            data=open('../fetched-pages/test/'+f).read()
        except:
            continue
        if(len(data)>0):
            for line in data.split('\n'):
                t.AddDoc(line)
    #t.PrintStripGrams()
    print ' '.join(t.OutputDocs())





if(__name__=='__main__'):
    main()
