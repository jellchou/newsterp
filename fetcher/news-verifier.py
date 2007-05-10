#! /usr/bin/python2.4
#
#

import urllib2
import threading
import util
import workerPool
import bayesClassifier


""" This class will download a set the set of links found from
each RSS feed and for each page guess if it is a news article.
If the majority of links are not news articles, a failure
will be lodged for this RSS page. 
"""

__author__ = 'jhebert@cs.washington.edu (Jack Hebert)'



class NewsVerifier:
    def __init__(self):
        self.classifier = bayesClassifier.BayesClassifier()
        self.fetcherPool = FetcherPool(self.classifier)

    def Init(self):
        self.classifier.LoadModel('model.test')

    def RunGolden(self):
        toFetch = open('golden-news.out').read().split('\n')
        self.fetcherPool.urlsToFetch = toFetch
        self.noVerify = True
        self.fetcherPool.run()

    def RunPyrite(self):
        toFetch = open('possible-news.out').read().split('\n')
        self.fetcherPool.urlsToFetch = toFetch
        self.noVerify = False
        self.fetcherPool.run()

    def Run(self):
        self.RunGolden()

    
class FetcherPool:
    def __init__(self, classifier):
        self.classifier = classifier
        self.urlsToFetch = []
        self.numThreads = 20
        self.threadPool = workerPool.WorkerPool(self.numThreads)
        self.mutexLock = threading.Lock()
        self.noVerify = False

    def GetWorkItem(self):
        toReturn = None
        self.mutexLock.acquire()
        if(len(self.urlsToFetch)>0):
            toReturn = self.urlsToFetch[0]
            self.urlsToFetch = self.urlsToFetch[1:]
        self.mutexLock.release()
        return toReturn

    def ReturnResults(self, pages, numPossible):
        self.mutexLock.acquire()
        # TODO: do something with the results.
        self.mutexLock.release()

    def run(self):
        for i in range(self.numThreads):
            self.threadPool.startWorkerJob('!', FetcherAgent(self, self.classifier))
        self.threadPool.wait()


                                           
class FetcherAgent:
    def __init__(self, pool, classifier):
        self.master = pool
        self.classifier = classifier

    def run(self):
        while(True):
            job, toReturn = self.master.GetWorkItem(), []
            if(job==None):
                break
            urls = job.split('\t')
            for url in urls:
                try:
                    page = self.FetchPage(url)
                    doc = util.SplitToWords(page)
                    if(self.master.noVerify):
                        toReturn.append(page)
                    elif(self.classifier.ClassifyDoc(doc)):
                        toReturn.append(page)
                except ValueError:
                    print 'Could not fetch: ', url, ' ValueError.'
                except urllib2.URLError:
                    print 'Could not fetch: ', url, ' URLError.'
                except:
                    print 'Could not fetch: ', url, ' unknown error.'
            self.master.ReturnResults(toReturn, len(items))


    def FetchPage(self, url):
        userAgent = 'NewsTerp - jhebert@cs.washington.edu'
        return util.FetchPage(url, userAgent)



def main():
    n = NewsVerifier()
    n.Run()

main()
