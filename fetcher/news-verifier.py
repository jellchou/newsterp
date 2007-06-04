#! /usr/bin/python2.4
#
#

import socket
import urllib2
import httplib
import threading
import util
import workerPool
import bayesClassifier
from templateStripper import TemplateStripper

""" This class will download a set the set of links found from
each RSS feed and for each page guess if it is a news article.
If the majority of links are not news articles, a failure
will be lodged for this RSS page. 
"""

__author__ = 'jhebert@cs.washington.edu (Jack Hebert)'

# TODO: have this also classifiy news docs!
#       Just need to mark pos and neg, build the model



# TODO: BadStatusLine error



class NewsVerifier:
    def __init__(self):
        self.txtClassifier = bayesClassifier.BayesClassifier()
        self.classifier = bayesClassifier.BayesClassifier()
        self.fetcherPool = FetcherPool(self.classifier,
                                       self.txtClassifier)

    def Init(self):
        self.txtClassifier.LoadModel('txt-vs-bin.model')
        #self.classifier.LoadModel('model.test')

    def RunGolden(self):
        toFetch = open('golden-news.out').read().split('\n')
        self.fetcherPool.SetUrls(toFetch, True)
        self.fetcherPool.run()

    def RunPyrite(self):
        toFetch = open('pyrite-news.out').read().split('\n')
        self.fetcherPool.SetUrls(toFetch, False)
        self.fetcherPool.run()

    def Run(self):
        self.RunGolden()
        #self.RunPyrite()

    
class FetcherPool:
    def __init__(self, classifier, txtClassifier):
        self.classifier = classifier
        self.txtClassifier = txtClassifier
        self.blacklist = util.LoadFileToHash('blacklist.txt')
        self.urlsToFetch = []
        self.numThreads = 30
        self.numDone = 0
        self.done = {}
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
        frac1 =  str(len(pages)) +  '/' + str(numPossible)
        frac2 = str(self.numDone) + '/' + str(self.numToDo)
        print 'Done: ', frac2, ':', frac1, rssPage
        if(len(pages)>0):
            if(self.noVerify):
                newsType = 'golden'
            else:
                newsType = 'pyrite'
            fileName = str(abs(hash(rssPage)))
            pathToFile = '../fetched-pages/'+newsType+'/'+fileName
            f = open(pathToFile, 'a')
            f.write('\n'.join(pages+['']))
            f.close()

            data=open(pathToFile).read()
            t = TemplateStripper()
            for line in data.split('\n'):
                t.AddDoc(line)
            f = open(pathToFile, 'w')
            f.write('\n'.join(t.OutputDocs()))
            f.close()

            
        self.mutexLock.release()

    def run(self):
        for i in range(self.numThreads):
            self.threadPool.startWorkerJob('!', FetcherAgent(self, self.classifier))
        self.threadPool.wait()
        self.threadPool.stop()

    def Blacklist(self, url):
        self.mutexLock.acquire()
        self.blacklist[url] = None
        self.mutexLock.release()

    def HasBeenDone(self, page):
        num = hash(page)
        self.mutexLock.acquire()
        if(num in self.done):
            toReturn = True
        else:
            self.done[num] = None
            toReturn = False
        self.mutexLock.release()
        return toReturn


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
            for url in urls:
                try:
                    if(url in self.master.blacklist):
                        continue
                    else:
                        self.master.Blacklist(url)
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
                    txt=self.master.txtClassifier.ClassifyDoc(page)
                    #print 'Classified:', page
                    #print 'Result:', txt
                    #print self.master.txtClassifier.ToString()
                    if(txt==0):
                        print 'This doc is not text!'
                        continue
                    if(self.master.HasBeenDone(page)):
                        continue
                    doc = util.SplitToWords(page)
                    #if(self.master.noVerify):
                    #    toReturn.append(url+'\t'+page)
                    #elif(self.classifier.ClassifyDoc(doc)):
                    # TODO: train this classifier.
                    toReturn.append(url+'\t'+page)
                except ValueError:
                    print ' Could not fetch: ', url, ' from: ', rssPage,  ' ValueError.'
                except urllib2.URLError, e:
                    reason = '\n URLError'
                    if('code' in dir(e)):
                        reason =  '\n URLError : ' + str(e.code)
                    elif('reason' in dir(e)):
                        reason = '\n URLError : ' + str(e.reason)
                    else:
                        print dir(e)
                    print ' Could not fetch: ', url, '\n from: ', rssPage, reason
                except socket.timeout:
                    print ' Could not fetch: ', url, '\n from: ', rssPage, '\n socket timeout.'
                except httplib.InvalidURL:
                    pass
                except httplib.BadStatusLine:
                    pass
            self.master.ReturnResults(rssPage, toReturn, len(items))


    def FetchPage(self, url):
        userAgent = 'NewsTerp - jhebert_at_cs washington edu'
        return util.FetchPage(url, userAgent)



def main():
    n = NewsVerifier()
    n.Init()
    n.Run()

main()

