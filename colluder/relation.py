#! /usr/bin/python2.4



__author__ = 'jhebert@cs.washington.edu (Jack Hebert)'



""" Hold relation text, pointer back to article. """



class Relation:
    def __init__(self, dataLine, articleURL):
        end = dataLine.find(')+[snum:')
        self.success = not (end==-1)
            
        text = dataLine[:end]
        for char in ',.()[]':
            text = text.replace(char, ' ')
        self.relation = text
        end2 = dataLine.find('hrs:', end)
        try:
            self.sentenceNumber = int(dataLine[end+8:end2-1])
        except:
            print 'Failed parsing:', dataLine
            print 'Got it down to:', dataLine[end+8:end2]
        self.originalSentence = dataLine[end2+4:-2]
        self.articleURL = articleURL.replace('"', '')
        #print 'Article:', self.articleURL
        try:
            if(self.articleURL[-1]=='/'):
                self.articleURL = self.articleURL[:-1]
        except:
            self.articleURL = ''
            #print 'Line:', dataLine
            #print 'ArticleURL:', articleURL

    def ToString(self):
        return ''.join([self.articleURL, ' : ', self.relation])

    def RelationAsText(self):
        toReturn = self.relation
        for char in '();.&#$':
            toReturn = toReturn.replace(char, '')
        return toReturn

    def RelationSentence(self):
        return self.originalSentence

    def SentenceNumber(self):
        return self.sentenceNumber
