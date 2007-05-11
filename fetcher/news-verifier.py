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
        self.fetcherPool.noVerify = True
        self.fetcherPool.run()

    def RunPyrite(self):
        toFetch = open('possible-news.out').read().split('\n')
        self.fetcherPool.urlsToFetch = toFetch
        self.fetcherPool.noVerify = False
        self.fetcherPool.run()

    def Run(self):
        self.RunGolden()

    
class FetcherPool:
    def __init__(self, classifier):
        self.classifier = classifier
        self.urlsToFetch = []
        self.numThreads = 20
        self.numDone = 0
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

    def ReturnResults(self, rssPage, pages, numPossible):
        self.mutexLock.acquire()
        self.numDone += 1
        frac =  str(len(pages)) +  '/' + str(numPossible)
        print 'Done: ', self.numDone, ':', frac
        if(len(pages)>0):
            if(self.noVerify):
                print 'Golden!'
                newsType = 'golden'
            else:
                print 'Pyrite!'
                newsType = 'pyrite'
            fileName = hash(rssPage)
            if(fileName < 0):
                fileName = -fileName
            fileName = str(fileName)
            f = open('../fetched-pages/html/'+newsType+'/'+fileName, 'a')
            f.write('\n'.join(pages+['']))
            f.close()
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
            items = job.split('\t') 
            rssPage, urls = items[0], util.FilterListToUnique(items[1:])
            urls = 
            for url in urls:
                try:
                    print '  Fetching: ', url
                    page = self.FetchPage(url).replace('\n', '\t')
                    doc = util.SplitToWords(page)
                    if(self.master.noVerify):
                        toReturn.append(url+'\t'+page)
                    elif(self.classifier.ClassifyDoc(doc)):
                        toReturn.append(url+'\t'+page)
                except ValueError:
                    print ' Could not fetch: ', url, ' ValueError.'
                except urllib2.URLError:
                    print ' Could not fetch: ', url, ' URLError.'
                except:
                    print ' Could not fetch: ', url, ' unknown error.'
            self.master.ReturnResults(rssPage, toReturn, len(items))


    def FetchPage(self, url):
        userAgent = 'NewsTerp - jhebert@cs.washington.edu'
        return util.FetchPage(url, userAgent)



def main():
    n = NewsVerifier()
    n.Run()

main()
