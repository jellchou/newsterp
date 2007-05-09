#!/usr/bin/python2.4
#
# Copyright 2006 Google, Inc. All Rights Reserved.

import time
import Queue
import threading
import workerThread

__author__ = "jhebert@cs.washington.edu (Jack Hebert)"

"""Class to hold information for a set of workerThreads.
Still need to solve the problem of when the threads are done working. """
class WorkerPool:

  def __init__(self, numOfThreads):
    self.logging = Queue.Queue()
    self.freeWorkerQueue = Queue.Queue()
    self.jobQueue = Queue.Queue()
    self.busyCount = 0 # A count of how many workers are busy.
    self.workers = []  # An array of workerThread objects.
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

  """ This should return once all of the worker threads are done
  and there are no more jobs to execute. """
  def wait(self):
    while(not self.__done()):
      time.sleep(.5)

  def __done(self):
    for worker in self.workers:
      busy, alive = worker.busy, worker.thread.isAlive()
      empty = worker.workQueue.empty()
      if((busy)&(alive)):
        return False
      elif((alive)&(not empty)):
        return False
    return True
