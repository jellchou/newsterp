#! /usr/bin/python2.4



""" This file contains several methods that are repeatedly used
in downloading the rss feeds, parsing them, creating stopLists
and such. """

__author__ = 'jhebert@cs.washington.edu (Jack Hebert)'

import os
import socket
import urllib2
socket.setdefaulttimeout(10)

class StopList:
    def __init__(self):
        self.tokens = {}

    def AddWord(self, token):
        if(not token in self.tokens):
            self.tokens[token] = 0
        self.tokens[token] += 1

    def GetTopN(self, n):
        items = []
        for token in self.tokens:
            items.append((-self.tokens[token], token))
        items.sort()
        return [item[1] for item in items[:n]]


def FetchPage(url, userAgent):
    if(url.find('http')==-1):
        url = 'http://'+url
    request = urllib2.Request(url)
    request.add_header('User-Agent', userAgent)
    opener = urllib2.build_opener()
    data = opener.open(request).read()
    data = data.replace('&nbsp;', ' ')
    data = data.replace('&quot;', '"')
    data = data.replace('&amp;', '&')
    data = data.replace('&gt;', '>')
    data = data.replace('&lt;', '<')

    return data

def IsRSS(page):
    if(page.find('<rss version')>-1):
        return True
    elif(page.find('<?xml version')>-1):
        return True
    return False

def FilterListToUnique(list, filterSet={}):
    t = {}
    for item in list:
        if(not item in filterSet):
            t[item] = None
    return t.keys()

def LoadFileToHash(fileName):
    toReturn = {}
    try:
        data = open(fileName).read().split('\n')
        for line in data:
            toReturn[line] = None
    except IOError:
        pass
    return toReturn


def SplitByTokens(data, splitters):
    toReturn, tokens = [], [ord(a) for a in splitters]
    prev, curr, length1, length2 = 0, 0, len(data), len(tokens)
    tokens.sort()
    #print 'Tokens:', tokens
    while(curr < length1):
        i, toFind = 0, ord(data[curr])
        #print 'ToFind:', toFind
        while(i < length2):
            if(toFind == tokens[i]):
                #print 'Splitting on a token.'
                if(curr > prev):
                    toReturn.append(data[prev:curr])
                prev = curr+1
                break
            elif(toFind < tokens[i]):
                #print 'Token not found.', i
                break
            i += 1
        curr += 1
    toReturn.append(data[prev:])
    return toReturn


def SplitToWords(doc):
    tokens = '<>,.;/\t\n"\'[]\\{}?!@!&$%()-_=+ '
    return SplitByTokens(doc, tokens)

def LoadAndSplitFromDir(pathToDir):
    toReturn, files = [], os.listdir(pathToDir)
    for f in files:
        try:
            data = open(pathToDir+'/'+f).read().lower().split('\n')
            toAdd = []
            for line in data:
                line = SplitToWords(line)
                toAdd += line
            toReturn.append(toAdd)
        except IOError:
            pass
    return toReturn

def EscapeDelimit(page, start, end, f = lambda x: True):
    toReturn = []
    prevEnd = -len(end)
    startIndex = page.find(start)
    enderIndex = page.find(end, startIndex+len(start))
    if(startIndex==-1):
        return page
    if(enderIndex==-1):
        return page[:startIndex]
    while(enderIndex > 0):
        toAdd = page[prevEnd+len(end):startIndex]
        if(f(toAdd)):
            toReturn.append(toAdd)
            toReturn.append(' ')
        prevEnd = enderIndex
        startIndex = page.find(start, enderIndex+len(end))
        if(startIndex==-1):
            toAdd = page[enderIndex+len(end):]
            if(f(toAdd)):
                toReturn.append(toAdd)
            break
        enderIndex = page.find(end, startIndex+len(start))
    return ''.join(toReturn)

def CollapseWhitespace(page):
    return ' '.join(page.split())
    
