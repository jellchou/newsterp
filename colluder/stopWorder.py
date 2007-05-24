#! /usr/bin/python2.4



__author__ = 'jhebert@cs.washington.edu (Jack Hebert)'




class StopWorder:
    def __init__(self):
        self.stopWords = {}

    def Init(self):
        data=open('stoplist.txt').read().split('\n')
        for word in data:
            if(len(word)>0):
                self.stopWords[word]=None

    def IsAStopWord(self, word):
        lowWord=''.join([a.lower() for a in word if a.isalpha()])
        return lowWord in self.stopWords

    def CleanText(self, text):
        toReturn, words = [], text.split()
        for word in words:
            if(not(self.IsAStopWord(word))):
                toReturn.append(word)
        return ' '.join(toReturn)


def main():
    s = StopWorder()
    s.Init()
    print s.CleanText('Hello, my name is jack')



if(__name__=='__main__'):
    main()
