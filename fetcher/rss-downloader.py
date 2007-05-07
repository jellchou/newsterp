#! /usr/bin/python2.4




import httplib
httplib.HTTPConnection.debuglevel = 1                             
import urllib2




request = urllib2.Request('http://google.com/') 
request.add_header('User-Agent', '')
opener = urllib2.build_opener()                                   
feeddata = opener.open(request).read()                            

print feeddata
