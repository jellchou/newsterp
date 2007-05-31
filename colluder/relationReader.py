#! /usr/bin/python2.4


from relation import Relation


__author__ = 'jhebert@cs.washington.edu (Jack Hebert)'



""" This class reads in relations from Colin's relation
format. It tries to nicely buffer reading from disk, but
like any of this really matters. """

class RelationReader:
    def __init__(self):
        self.fileName = ''
        self.filePointer = None
        self.currentArticle = ''
        

    def Open(self, fileName):
        """ Open a new relation file, possibly close
        an already open file for nicety."""
        self.fileName = fileName
        if(not(self.filePointer==None)):
            self.filePointer.close()
        self.filePointer = open(self.fileName)

    def ReadNextRelation(self):
        """ Read in the next relation, might
        need to read from disk, might have it
        in the buffer. """
        if(self.filePointer == None):
            return None
        line = self.filePointer.readline()
        if(line!=None):
            line = line.strip()
        else:
            return None
        if(len(line)==0):
            return None
        elif(line.find('set ')==0):
            self.currentArticle = line[5:-2]
            return self.ReadNextRelation()
        elif(line.find('endset;')==0):
            return self.ReadNextRelation()
        else:
            end = line.find('+')
            return Relation(line[1:end-1], self.currentArticle)


def main():
    r = RelationReader()
    r.Open('./../engine/relations.dat')
    rel = r.ReadNextRelation()
    while(rel != None):
        print rel.ToString()
        rel = r.ReadNextRelation()


if(__name__=='__main__'):
    main()
