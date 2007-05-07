#! /usr/bin/python2.4
#
#

import urllib2
import httplib

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


class PageFetcher:
    def __init__(self):
        self.doneFetching = False
        self.page = ''

    def FetchPage(self, urlToFetch):
        request = urllib2.Request(urlToFetch)
        request.add('User-Agent', 'NewsTerp - jhebert@cs.washington.edu')
        opener = urllib2.build_opener()
        self.page = opener.open(request).read() 
        self.doneFetching = True

    def DoneFetching(self):
        return self.doneFetching

    def IsRSS(self):
        if(self.page.find('<rss version')>-1):
            return True
        elif(self.page.find('<?xml version')>-1):
            return True
        return False

    def ExtractLinks(self):
        # TODO: extract and return all links from this page.

    def GetPage(self):
        return self.page


    
def main():
    a = AskGoogle()
    links = a.Run()
    # TODO: use pageFetcher to grab all of these pages.
    # TODO: of the pages that aren't RSS, try all pages within a click.
    # TODO: try all pages within 2 clicks?
    # TODO: multithread pageFetcher just a little bit.
    # TODO: write out all of the pages that are RSS or XML feeds.

main()
