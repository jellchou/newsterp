#! /usr/bin/python2.4

import os

__author__ = 'jhebert@cs.washington.edu (Jack Hebert)'

""" This script takes the files in the subdirectory and prefixes the current
data with a fake url and replaces all line-breaks with tabs. This then follows
the defined format for input to the relation-extractor.

Running it multiple times on the same text file will prefix with several urls.
This will still be readable by the relation extractor, but will have noisier
data as the urls will NEVER be filtered out. """

for path in ['./topic1/','./topic2/','./topic3/','./topic4/']:
    files = os.listdir(path)
    for f in files:
        filePath = path+f
        if(f.find('txt')==-1):
            continue
        try:
            print 'Working on:', filePath
            data=open(filePath).read()
            data = data.replace('\n', '\t')
            f = open(filePath,'w')
            f.write('http://'+filePath+'\t'+data)
            f.close()
        except IOError:
            print path+f
                
