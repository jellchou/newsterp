#!/usr/bin/python2.4
#
# Copyright 2006 Google, Inc. All Rights Reserved.

import threading, workerThread, Queue

__author__ = "jhebert@google.com (jhebert@google.com)"

"""Class to hold information for a set of workerThreads.
Still need to solve the problem of when the threads are done working. """
class WorkerPool:

  def __init__(self, numOfThreads):
    self.logging = Queue.Queue()
    self.freeWorkerQueue = Queue.Queue()
    self.jobQueue = Queue.Queue()
    # A count of how many workers are busy.
    self.busyCount = 0
    # An array of workerThread objects.
    self.workers = []
    for i in range(numOfThreads):
      try:
        w = workerThread.workerThread(self)
        t = threading.Thread(target = w.run)
        w.setThread(t)
        self.workers.append(w)
        self.freeWorkerQueue.put(w)
        t.start()
      except:
        continue

  """Iterate through the workers and create threads for those
  that are not alive."""
  def refreshWorkerPool(self):
    for worker in self.workers:
      if(not(worker.thread.isAlive())):
        t = threading.Thread(target = w.run)

  """ Add a job to the pool. If there is a free worker, immediately
  assign it to the job, otherwise put the job on the job queue. """
  def startWorkerJob(self, name, prog):
    try:
      w = self.freeWorkerQueue.get(False)
      w.addToQueue(name, prog)
    except:
      self.jobQueue.put((name, prog))

  """ Called by a returning worker.
  If there is a free job, then keep the worker working.
  Otherwise put the worker on the free queue. """
  def returnWorker(self, worker):
    try:
      j = self.jobQueue.get(False)
      name, prog = j
      worker.addToQueue(name, prog)
    except:
      self.freeWorkerQueue.put(worker)

  """ Sleep until no threads are working and the jobQueue is empty.
  Then tell each worker to stop working. Maybe I should also join
  all of the threads? """
  def stop(self):
    for w in self.workers:
      w.stop()
