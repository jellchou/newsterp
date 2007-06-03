#! /usr/bin/python2.4



__author__ = 'jhebert@cs.washington.edu (Jack Hebert)'



""" Hold relation text, pointer back to article. """



class Relation:
    def __init__(self, dataLine, articleURL):
        items = dataLine.split(',')
        if(len(items)<3):
            self.success = False
            return
        else:
            self.success = True
        self.np1 = items[0]
        self.relation = items[1]
        self.np2 = ' '.join(items[2:])
        self.data = dataLine
        self.articleURL = articleURL
        if(self.articleURL[-1]=='/'):
            self.articleURL = self.articleURL[:-1]

    def ToString(self):
        return ''.join([self.articleURL, ' : ', self.np1, ':',
                        self.relation, ':', self.np2])

    def RelationAsText(self):
        return ' '.join([self.np1, self.relation, self.np2])

    def CanMerge(self, relation):
        """ Can they be merged? """
        return False


    def Merge(self):
        """ Merge these two articles. Return a new
        article. """
        pass
