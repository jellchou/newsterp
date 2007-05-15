#!/usr/bin/python2.4
#
# Copyright 2006 Google, Inc. All Rights Reserved.

import sys
import traceback
import threading
import Queue

__author__ = "Jack Hebert (jhebert@cs.washington.edu)"

class workerThread:
  """One thread per workerThread class will perfrom the
  computations for the nodes. """

  def __init__(self, threadPool):
    self.threadPool = threadPool
    self.workQueue = Queue.Queue()
    self.busy = False
    self.thread = None

  def addToQueue(self, flag, item):
    """ Add a job to run onto the work queue for this worker. """
    self.workQueue.put((flag, item))

  """Set the thread that will be be running this queue.
  Not needed to run, but might be helpful somehow. """
  def setThread(self, thread):
    self.thread = thread

  """ Insert a job into the queue telling the thread to quit. """
  def stop(self):
    self.workQueue.put(('quit', exiter(self.threadPool)))

  """Block until there is a waiting job on the queue, and then
  execute the run method of that object. Quits are done by
  inserting an 'exiter' object into the queue. """
  def run(self):
    while(1):
      self.threadPool.logging.put('waiting on queue')
      flag, item = self.workQueue.get() # this line blocks!
      toAppend = str(item)+', '+str(flag)
      self.busy = True
      status = 'workerThread is running:'+toAppend
      self.threadPool.logging.put(status)
      try:
        item.run()
      except:
        if(flag == 'quit'):
          sys.exit(0)
        else:
          print "Exception in user code:"
          print '-'*60
          traceback.print_exc(file=sys.stdout)
          print '-'*60
      self.busy = False
      status = 'workerThread done running:'+toAppend
      self.threadPool.logging.put(status)
      self.threadPool.returnWorker(self)


"""When run this class exits the thread without error.
This allows the main run loop to always execute the object
and not check for a runtime exit flag. Slightly more graceful."""
class exiter:
  def __init__(self, threadPool):
    self.threadPool = threadPool
  def run(self):
    self.threadPool.logging.put('worker is exiting')
    sys.exit(0)
