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
        self.links = []
        self.successPages = []
        self.failedPages = []
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

    def ReturnLinks(self, links, url, success):
        self.mutexLock.acquire()
        if(success):
            self.links += links
            self.successPages.append(url)
        else:
            self.failedPages.append(url)
        self.numDone += 1
        frac = str(self.numDone)+'/'+str(self.numToDo)
        dec = str(float(self.numDone) / (self.numToDo+1))
        print frac, dec
        self.mutexLock.release()
        if(self.numDone%20==0):
            self.WriteResults()

    def WriteResults(self):
        print 'Writing results to disk...'
        self.mutexLock.acquire()
        self.WriteToFile(self.successPages, 'success.out')
        self.WriteToFile(self.failedPages, 'failures.out')
        self.WriteToFile(self.links, 'links.out')
        self.successPages, self.failedPages, self.links = [], [], []
        self.mutexLock.release()

    def WriteToFile(self, data, name):
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
        while(len(self.urls)>0):
            time.sleep(.5)
        time.sleep(1)


class RssFetcher:
    def __init__(self, master):
        self.master = master

    def run(self):
        while(True):
            url, success = self.master.GetUrl(), False
            if(len(url)==0):
                break
            try:
                page = self.FetchPage(url)
                links = self.ExtractLinks(page)
                success = True
            except ValueError:
                print 'Could not fetch: ', url, ' ValueError.'
            except urllib2.URLError:
                print 'Could not fetch: ', url, ' URLError.'
            except:
                print 'Could not fetch: ', url, ' unknown error.'
            if(success):
                self.master.ReturnLinks(links, url, True)
            else:
                self.master.ReturnLinks([], url, False)

    def FetchPage(self, url):
        if(url.find('http')==-1):
            url = 'http://'+url
        request = urllib2.Request(url)
        request.add_header('User-Agent','NewsTerp - jhebert@cs.washington.edu')
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
            if(link.find('<')>-1):
                link = link[:link.find('<')]
            toReturn.append(link)
            index = xml.find('http://', end)
        return toReturn



def main():
    pages = open('rss.out').read().split('\n')
    f = FetcherPool(pages)
    f.run()
    f.wait()



main()
