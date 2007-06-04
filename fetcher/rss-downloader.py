#! /usr/bin/python2.4
#
#

import time
import socket
import httplib
import urllib2
import threading
import workerPool
import util
socket.setdefaulttimeout(30)

""" This class should iterate over the list of RSS pages and download them.
Should look for new RSS pages since the last download, or specifically new
links on them since the last download. """

__author__ = "jhebert@cs.washington.edu (Jack Hebert)"



# TODO: move all URL error handling to util.
# TODO: try and make it do redirects as I feel like
#       it is missing some stuff it should get.

class FetcherPool:
    def __init__(self, urls, golden):
        self.threadPool = workerPool.WorkerPool(10)
        self.mutexLock = threading.Lock()
        self.linkSets = []
        self.golden = golden
        self.successPages = []
        self.failedPages = []
        self.failedFeeds = util.LoadFileToHash('blacklist.txt')
        self.urls = util.FilterListToUnique(urls, self.failedFeeds)
        self.numToDo = len(urls)
        self.numDone = 0
        self.ClearResults()

    def GetUrl(self):
        toReturn = None
        self.mutexLock.acquire()
        if(len(self.urls)>0):
            toReturn = self.urls[0]
            self.urls = self.urls[1:]
        self.mutexLock.release()
        return toReturn

    def ReturnLinks(self, links, url):
        self.mutexLock.acquire()
        if(len(links)>0):
            toAppend = '\t'.join([url]+links)
            self.linkSets.append(toAppend)
            self.successPages.append(url)
        else:
            self.failedPages.append(url)
        self.numDone += 1
        self.PrintStatus()
        self.mutexLock.release()
        if(self.numDone%20==0):
            self.WriteResults()

    def PrintStatus(self):
        frac = str(self.numToDo-len(self.urls))+'/'+str(self.numToDo)
        dec = str(float(self.numDone) / (self.numToDo+1))
        print frac, dec

    def WriteResults(self):
        print 'Writing results to disk...'
        self.mutexLock.acquire()
        self.WriteToFile(self.successPages, 'success.out')
        self.WriteToFile(self.failedPages, 'failures.out')
        if(self.golden):
            self.WriteToFile(self.linkSets, 'golden-news.out')
        else:
            self.WriteToFile(self.linkSets, 'pyrite-news.out')
        self.successPages, self.failedPages, self.linkSets = [], [], []
        self.mutexLock.release()

    def WriteToFile(self, data, name):
        data = util.FilterListToUnique(data)
        if(len(data)==0):
            return
        f = open(name, 'a')
        f.write('\n'.join(data))
        f.write('\n')
        f.close()

    def ClearResults(self):
        if(self.golden):
            f = open('golden-news.out' ,'w')
        else:
            f = open('pyrite-news.out' ,'w')
        f.close()
        


    def run(self):
        print '******\n******'
        print len(self.urls), ' pages to fetch.'
        print '******\n******'
        time.sleep(2)
        for i in range(10):
            self.threadPool.startWorkerJob('!', RssFetcher(self))
        self.threadPool.wait()
        self.threadPool.stop()
        self.WriteResults()




class RssFetcher:
    def __init__(self, master):
        self.master = master

    def run(self):
        while(True):
            url, links = self.master.GetUrl(), []
            if(url==None):
                break
            elif(len(url)<3):
                break
            try:
                url = url.strip()
                page = self.FetchPage(url)
                links = self.ExtractLinks(page)
            except ValueError:
                print 'Could not fetch: ', url, ' ValueError.'
            except urllib2.URLError:
                print 'Could not fetch: ', url, ' URLError.'
            #except:
            #    print 'Could not fetch: ', url, ' unknown error.'
            self.master.ReturnLinks(links, url)

    def FetchPage(self, url):
        userAgent = 'NewsTerp - jhebert_at_cs washington edu'
        return util.FetchPage(url, userAgent)

    def ExtractLinks(self, xml):
        toReturn = []
        index = xml.find('http://')
        while(index > -1):
            charDelim = xml[index-1]
            end = xml.find(charDelim, index+1)
            link = xml[index:end]
            breakers = [' ','<','[',']',')','(','\t','\n','"']
            for char in breakers:                
                if(link.find(char)>-1):
                    link = link[:link.find(char)]
            link = link.strip()
            if(len(link)>1):
                toReturn.append(link)
                end = index + len(link)+1
            index = xml.find('http://', index+1)
        return toReturn



def main():
    #pages = open('rss.out').read().split('\n')
    pages = open('golden-list.txt').read().split('\n')
    f = FetcherPool(pages, True)
    f.run()



main()
