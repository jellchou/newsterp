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

# TODO: have this also classifiy news docs!

class NewsVerifier:
    def __init__(self):
        self.txtClassifier = bayesClassifier.BayesClassifier()
        self.classifier = bayesClassifier.BayesClassifier()
        self.fetcherPool = FetcherPool(self.classifier,
                                       self.txtClassifier)

    def Init(self):
        self.txtClassifier.LoadModel('txt-vs-bin.model')
        self.classifier.LoadModel('model.test')

    def RunGolden(self):
        toFetch = open('golden-news.out').read().split('\n')
        self.fetcherPool.SetUrls(toFetch, True)
        self.fetcherPool.run()

    def RunPyrite(self):
        toFetch = open('possible-news.out').read().split('\n')
        self.fetcherPool.SetUrls(toFetch, False)
        self.fetcherPool.run()

    def Run(self):
        self.RunGolden()

    
class FetcherPool:
    def __init__(self, classifier, txtClassifier):
        self.classifier = classifier
        self.txtClassifier = txtClassifier
        self.urlsToFetch = []
        self.numThreads = 30
        self.numDone = 0
        self.numToDo = 0
        self.threadPool = workerPool.WorkerPool(self.numThreads)
        self.mutexLock = threading.Lock()
        self.noVerify = False

    def SetUrls(self, urls, noVerify):
        self.numToDo = len(urls)
        self.urlsToFetch = urls
        self.noVerify = noVerify

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
        print 'Done: ', self.numDone, '/', self.numToDo, ':', frac
        if(len(pages)>0):
            if(self.noVerify):
                newsType = 'golden'
            else:
                newsType = 'pyrite'
            fileName = str(abs(hash(rssPage)))
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
            print ' Working on: ', rssPage
            for url in urls:
                try:
                    url = url.replace('&amp;', '&')
                    url = url.replace('amp;', '&')
                    fileType = url[-3:].lower()
                    if((fileType=='gif')|(fileType=='jpg')):
                        continue
                    f = lambda x : len(x.split())>3
                    page = self.FetchPage(url).replace('\n', '\t')
                    page = util.EscapeDelimit(page, '<script', '</script>',f )
                    page = util.EscapeDelimit(page, '<SCRIPT', '</SCRIPT>', f)
                    page = util.EscapeDelimit(page, '<noscript', '</noscript>', f)
                    page = util.EscapeDelimit(page, '<NOSCRIPT', '</NOSCRIPT>', f)
                    page = util.EscapeDelimit(page, '<style', '</style>', f)
                    page = util.EscapeDelimit(page, '<STYLE', '</STYLE>', f)
                    page = util.EscapeDelimit(page, '<', '>', f)
                    page = util.CollapseWhitespace(page)
                    txt=self.master.txtClassifer.ClassifyDoc([page])
                    if(not txt):
                        print 'This doc is not text!'
                        continue
                    doc = util.SplitToWords(page)
                    if(self.master.noVerify):
                        toReturn.append(url+'\t'+page)
                    elif(self.classifier.ClassifyDoc(doc)):
                        toReturn.append(url+'\t'+page)
                except ValueError:
                    print ' Could not fetch: ', url, ' from: ', rssPage,  ' ValueError.'
                except urllib2.URLError, e:
                    print ' Could not fetch: ', url, ' from: ', rssPage, ' URLError.'
                    #print e.code
                    #data = e.read()
                    #print len(data)
             
                except:
                    print ' Could not fetch: ', url, ' unknown error.'
            self.master.ReturnResults(rssPage, toReturn, len(items))


    def FetchPage(self, url):
        userAgent = 'NewsTerp - jhebert_at_cs washington edu'
        return util.FetchPage(url, userAgent)



def main():
    n = NewsVerifier()
    n.Run()

main()
