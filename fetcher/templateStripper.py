#! /usr/bin/python2.4


__author__ = 'jhebert@cs.washington.edu (Jack Hebert)'





class TemplateStripper:
    def __init__(self):
        self.gramCount = 0
        self.grams = {}
        self.docs = []

    def AddDoc(self, doc):
        self.docs.append(doc)
        words,window = doc.split(), []
        for word in words:
            window.append(word)
            if(len(window)==3):
                gram = ' '.join(window)
                if(not (gram in self.grams)):
                    self.grams[gram] = 0
                self.grams[gram] += 1
                self.gramCount += 1
            window=window[-2:]

    def OutputDocs(self):

        for doc in self.docs:
            words,window = doc.split(' '),[]
            for word in words:
                window.append(word)
                if(len(window)==3):
                    gram = ' '.join(window)
                    if(self.grams[gram]*2>self.gramCount):
                        window = []
                    else:
                        pass # TODO: output window[0]
                window=window[2:]
            # TODO: output window

    def ClearModel(self):
        self.docs = []
        self.grams = {}
