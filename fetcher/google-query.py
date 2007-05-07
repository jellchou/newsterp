#! /usr/bin/python2.4
#
#

import time
import urllib2
import httplib
import threading
import workerPool

''' This module will query google through Mark\'s tunnel to find
web pages with rss feeds listed. The pages will then be downloaded
and potential rss feeds parsed out. '''

__author__ = "jhebert@cs.washington.edu (Jack Hebert)"


class AskGoogle:
    ''' For queries to run we need the proxy server running. Ask Jack about this. '''
    def __init__(self):
        self.queries = ['news', 'local news', 'global news', 'international news',
                        'weather', 'business', 'tech', 'sports', 'fashion',
                        'Europe', 'Asia', 'US', 'cbs rss', 'abc', 'komo', 'kiro'
                        'times', 'financial', 'breaking news', 'popular news',
                        'most emailed news', 'newspaper']

    def Run(self):
        links = []
        for query in self.queries:
            print 'Running query:', query
            for i in range(10):
                links += self.GetLinks(self.SendQuery(query+' rss', str(10*i)))
                print len(links), ' links.'
        print '\n'.join(links)
        print len(links), ' links.'
        return self.FilterLinks(links)

    def FilterLinks(self, links):
        toReturn = {}
        for link in links:
            toReturn[link] = None
        return toReturn.keys()

    def SendQuery(self, query, start):
        try:
            queryString = '/search?q=%(queryString)s&%(otherArgs)s' % (
                { 'queryString' : query.replace(' ', '+'),
                  'otherArgs' : 'start='+start+'&ie=utf-8&oe=utf-8',})
            hostname, port = 'localhost', 5001
            url = 'http://%(hostName)s:%(portNumber)s%(queryString)s' % (
                { 'hostName': hostname,
                  'portNumber' : port,
                  'queryString': queryString, })
            f=urllib2.urlopen(url)
            data=f.read()
            print 'Length received:', len(data)
            return data
        except:
            print "I think I was 404'ed"
        return ''

    def GetLinks(self, page):
        toReturn = []
        results = page.split('<R>')
        for result in results[1:]:
            start = result.find('<U>')
            end = result.find('</U>', start)
            toReturn.append(result[start+3:end])
        return toReturn


class FetchPool:
    def __init__(self, links):
        self.urls = links
        self.results = []
        self.mutexLock = threading.Lock()
        self.threadPool = workerPool.WorkerPool(2)
        temp = {}
        for link in links:
            temp[link] = None
        self.urls = temp.keys()

    def GetUrl(self):
        toReturn = ''
        self.mutexLock.acquire()
        if(len(self.urls)>0):
            toReturn = self.urls[0]
            self.urls = self.urls[1:]
        self.mutexLock.release()
        return toReturn

    def AddResults(self, links):
        self.mutexLock.acquire()
        self.results += links
        print len(self.results), ' results.'
        self.mutexLock.release()

    def run(self):
        for i in range(20):
            self.threadPool.startWorkerJob('!', PageFetcher(self))
        self.threadPool.stop()

    def wait(self):
        while(len(self.urls)>0):
            time.sleep(.5)
        time.sleep(2)


class PageFetcher:
    def __init__(self, pool):
        self.fetchPool = pool
        self.baseUrl = ''
        self.page = ''

    def run(self):
        while(True):
            url = self.fetchPool.GetUrl()
            if(url==''):
                break
            try:
                self.FetchPage(url)
                results = self.ExtractLinks()
                self.fetchPool.AddResults(results)
            except ValueError:
                print 'Could not fetch: ', url
            except urllib2.URLError:
                print 'Could not fetch: ', url

    def FetchPage(self, urlToFetch):
        if(urlToFetch.find('http')==-1):
            urlToFetch = 'http://'+urlToFetch
        request = urllib2.Request(urlToFetch)
        request.add_header('User-Agent', 'NewsTerp - jhebert@cs.washington.edu')
        opener = urllib2.build_opener()
        self.page = opener.open(request).read() 
        self.baseUrl = urlToFetch

    def IsRSS(self):
        if(self.page.find('<rss version')>-1):
            return True
        elif(self.page.find('<?xml version')>-1):
            return True
        return False

    def ExtractLinks(self):
        toReturn = []
        links = self.page.split('<a href=')[1:]
        for link in links:
            if(link.find(' ')>-1):
                link = link[:link.find(' ')]
            openChar = link[0]
            if((openChar=='"')|(openChar=="'")):
                end = link.find(openChar, 2)
            else:
                end = link.find('>')
            try:
                toAdd = link[1:end]
                if((toAdd.find('http')!=0)&(toAdd.find('www')!=0)):
                    if(toAdd[0]=='/'):
                        toAdd = self.baseUrl + toAdd
                    else:
                        toAdd = self.baseUrl +'/' + toAdd
                toReturn.append(toAdd)
            except:
                pass
        return toReturn

    def GetPage(self):
        return self.page




def main():
    #a = AskGoogle()
    #links = a.Run()


    links = ['http://www.google.com', 'www.nytimes.com', 'www.cs.washington.edu']
    f = FetchPool(links)
    f.run()
    f.wait()

    print '*****'

    f2 = FetchPool(f.results)
    f2.run()
    f2.wait()

    print '*****'

    f3 = FetchPool(f2.results)
    f3.run()
    f3.wait()

    
    # TODO: of the pages that aren't RSS, try all pages within a click.
    # TODO: try all pages within 2 clicks?
    # TODO: multithread pageFetcher just a little bit.
    # TODO: write out all of the pages that are RSS or XML feeds.

main()
