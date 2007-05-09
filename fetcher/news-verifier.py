#! /usr/bin/python2.4
#
#

import urllib2
import util
import workerPool
import bayes-classifier


""" This class will download a set the set of links found from
each RSS feed and for each page guess if it is a news article.
If the majority of links are not news articles, a failure
will be lodged for this RSS page. 
"""

__author__ = 'jhebert@cs.washington.edu (Jack Hebert)'

# TODO: need to multi-thread the downloading of links.


class NewsVerifier:
    def __init__(self):
        self.classifier = BayesClassifier()
        self.fetcherPool = FetcherPool()

    def Init(self):
        self.classifier.LoadModel('model.test')

    
class FetcherPool:
    def __init__(self, classifier):
        self.classifier = classifier
        self.urlsToFetch = []
        self.numThreads = 20
        self.threadPool = workerPool.WorkerPool(self.numThreads)
        self.mutexLock = threading.Lock()

    def GetWorkItem(self):
        toReturn = None
        self.mutexLock.acquire()
        if(len(self.urlsToFetch>0)):
            toReturn = self.urlsToFetch[0]
            self.urlsToFetch = self.urlsToFetch[1:]
        self.mutexLock.release()
        return toReturn

    def ReturnResults(self):
        self.mutexLock.acquire()
        self.mutexLock.release()

    def run(self)
        for i in range(self.numThreads):
            self.threadPool.startWorkerJob('!', FetcherAgent(self, self.classifier))

                                           
class FetcherAgent:
    def __init__(self, pool, classifier):
        self.master = pool
        self.classifier = classifier

    def run(self):
        while(True):
            url, success = self.master.GetWorkItem(), False
            if(url==None):
                break
            try:
                page = self.FetchPage(url)
                doc = util.SplitToWords(page)
                isNews = self.classifier.ClassifyDoc(doc)
                success = True
            except ValueError:
                print 'Could not fetch: ', url, ' ValueError.'
            except urllib2.URLError:
                print 'Could not fetch: ', url, ' URLError.'
            except:
                print 'Could not fetch: ', url, ' unknown error.'
            self.master.ReturnLinks(links, url, success)


    def FetchPage(self, url):
        userAgent = 'NewsTerp - jhebert@cs.washington.edu'
        return util.FetchPage(url, userAgent)
