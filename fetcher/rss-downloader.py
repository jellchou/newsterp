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

# TODO: make a second module which downloads these links, classifies as news page
#       or not, and then if a majority are not news, marks this rss page as a fail.
#       After this module we should be fairly set in terms of finding news pages.
#       Also will have a golden list that we always download from.
#
# TODO: Then will need to start extracting news articles from HTML pages.
#       Not really sure how to do that yet.



__author__ = "jhebert@cs.washington.edu (Jack Hebert)"


class FetcherPool:
    def __init__(self, urls):
        self.threadPool = workerPool.WorkerPool(10)
        self.mutexLock = threading.Lock()
        self.linkSets = []
        self.successPages = []
        self.failedPages = []
        self.failedFeeds = util.LoadFileToHash('rss-blacklist.txt')
        self.urls = util.FilterListToUnique(urls, self.failedFeeds)
        self.numToDo = len(urls)
        self.numDone = 0

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
            toAppend = '\t'.join([url+'!!!']+links)
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
        frac = str(self.numDone)+'/'+str(self.numToDo)
        dec = str(float(self.numDone) / (self.numToDo+1))
        print frac, dec

    def WriteResults(self):
        print 'Writing results to disk...'
        self.mutexLock.acquire()
        self.WriteToFile(self.successPages, 'success.out')
        self.WriteToFile(self.failedPages, 'failures.out')
        self.WriteToFile(self.linkSets, 'possible-news.out')
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


    def run(self):
        print '******\n******'
        print len(self.urls), ' pages to fetch.'
        print '******\n******'
        time.sleep(2)
        for i in range(10):
            self.threadPool.startWorkerJob('!', RssFetcher(self))

    def wait(self):
        self.threadPool.wait()


class RssFetcher:
    def __init__(self, master):
        self.master = master

    def run(self):
        while(True):
            url, links = self.master.GetUrl().strip(), []
            if(url==None):
                break
            try:
                page = self.FetchPage(url)
                links = self.ExtractLinks(page)
            except ValueError:
                print 'Could not fetch: ', url, ' ValueError.'
            except urllib2.URLError:
                print 'Could not fetch: ', url, ' URLError.'
            except:
                print 'Could not fetch: ', url, ' unknown error.'
            self.master.ReturnLinks(links, url)

    def FetchPage(self, url):
        userAgent = 'NewsTerp - jhebert@cs.washington.edu'
        return util.FetchPage(url, userAgent)

    def ExtractLinks(self, xml):
        toReturn = []
        index = xml.find('http://')
        while(index > -1):
            charDelim = xml[index-1]
            end = xml.find(charDelim, index)
            link = xml[index:end]
            if(link.find(' ')>-1):
                link = link[:link.find(' ')]
            if(link.find('<')>-1):
                link = link[:link.find('<')]
            if(link.find('[')>-1):
                link = link[:link.find('[')]
            if(link.find(';')>-1):
                link = link[:link.find(';')]
            if(link.find(')')>-1):
                link = link[:link.find(')')]
            if(link.find('\t')>-1):
                link = link[:link.find('\t')]
            if(link.find('\n')>-1):
                link = link[:link.find('\n')]
            link = link.strip()
            if(len(link)>1):
                toReturn.append(link)
            index = xml.find('http://', end)
        return toReturn



def main():
    pages = open('rss.out').read().split('\n')
    f = FetcherPool(pages)
    f.run()
    f.wait()



main()
