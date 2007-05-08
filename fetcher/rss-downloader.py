#! /usr/bin/python2.4
#
#

import time
import socket
import httplib
import urllib2
import threading
import workerPool

socket.setdefaulttimeout(30)

""" This class should iterate over the list of RSS pages and download them.
Should look for new RSS pages since the last download, or specifically new
links on them since the last download. """

# TODO: only save unique links

__author__ = "jhebert@cs.washington.edu (Jack Hebert)"


class FetcherPool:
    def __init__(self, urls):
        self.threadPool = workerPool.WorkerPool(10)
        self.mutexLock = threading.Lock()
        self.rssPages = []
        temp = {}
        for url in urls:
            temp[url] = None
        self.urls = temp.keys()
        self.numToDo = len(urls)
        self.numDone = 0

    def GetUrl(self):
        toReturn = ''
        self.mutexLock.acquire()
        if(len(self.urls)>0):
            toReturn = self.urls[0]
            self.urls = self.urls[1:]
        self.mutexLock.release()
        return toReturn

    def ReturnLinks(self, links):
        self.mutexLock.acquire()
        self.rssPages += links
        self.numDone += 1
        frac = str(self.numDone)+'/'+str(self.numToDo)
        dec = str(float(self.numDone) / (self.numToDo+1))
        print frac, dec
        self.mutexLock.release()

    def run(self):
        for i in range(10):
            self.threadPool.startWorkerJob('!', RssFetcher(self))

    def wait(self):
        while(len(self.urls)>0):
            time.sleep(.5)
        time.sleep(1)


class RssFetcher:
    def __init__(self, master):
        self.master = master

    def run(self):
        while(True):
            url = self.master.GetUrl()
            if(len(url)==0):
                break
            try:
                page = self.FetchPage(url)
                links = self.ExtractLinks(page)
                self.master.ReturnLinks(links)
            except e:
                print 'Error:', e

    def FetchPage(self, url):
        if(url.find('http')==-1):
            url = 'http://'+url
        request = urllib2.Request(url)
        request.add_header('User-Agent', 'NewsTerp - jhebert@cs.washington.edu')
        opener = urllib2.build_opener()
        return opener.open(request).read() 

    def ExtractLinks(self, xml):
        toReturn = []
        index = xml.find('http://')
        while(index > -1):
            charDelim = xml[index-1]
            end = xml.find(charDelim, index)
            link = xml[index:end]
            if(link.find(' ')>-1):
                link = link[:link.find(' ')]
            toReturn.append(link)
            index = xml.find('http://', end)
        return toReturn



def main():
    pages = open('rss.out').read().split('\n')
    f = FetcherPool(pages)
    f.run()
    f.wait()



main()
