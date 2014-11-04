#!/usr/bin/env python

import json
import sys
from copy_runsjson import RunsJson
from copy_stopsjson import StopsJson

# stop json example:
# {"id":"2098","nextInRun":"/stops/2099","prevInRun":"","run":"/runs/10",
#  "staticTime":"480","station":"/stations/9","stationName":"aquilone",
#  "time":"480","url":"/stops/2098"}

class Stops:
  dayNumber = 1
  startingRunId = 0
  startingStopId = 0
  newStops = None
  oldStops = None

  def __init__(self, runsJSON, stopsJson, dayNumber=1):
    self.dayNumber = dayNumber
    self.startingStopId = stopsJson.startingStopId
    self.startingRunId = runsJson.startingRunId
    self.oldStops = stopsJson.stops

  def load_stops(self):
    stops = self.oldStops
    print "Stops: " + str(len(stops))
    self.newStops = []
    for s in stops:
      curStopId = int(s['id'])
      s['id'] = str(curStopId + self.startingStopId)
      print "%d -> %s" %(curStopId, s['id'])
      time = int(s['staticTime'])
      s['staticTime'] = str(time + (1440 * self.dayNumber))
      run = s['run']
      runId = int(run)
      newRunId = runId + self.startingRunId
      s['run'] = str(newRunId)
    
      try:
        prevRunId = int(s['prevInRun'])
        s['prevInRun'] = str(prevRunId + self.startingStopId)
      except KeyError, e:
        #print "no prev run for id %d" %newRunId
        pass
      try:
        nextRunId = int(s['nextInRun'])
        s['nextInRun'] = str(nextRunId + self.startingStopId)
      except KeyError, e:
        #print "no next run for id %d" %newRunId
        pass
      self.newStops.append(s);

  def save_changes(self):
    filename = "jsons/stops_day%d.json" %(self.dayNumber)
    print "writing " + filename
    with open(filename, 'w') as outfile:
      json.dump({"stopsObjectsList": self.newStops}, outfile)


if __name__ == '__main__':
  stops = stopsJson = runsJson = None
  if len(sys.argv) > 1:
    dayNumber = int(sys.argv[1])
    if len(sys.argv) > 2:
      stopsJson = StopsJson(sys.argv[2], dayNumber)
      runsJson = RunsJson(dayNumber=dayNumber)
    else:
      stopsJson = StopsJson(dayNumber=dayNumber)
      runsJson = RunsJson()
  else:
    dayNumber = 1
    stopsJson = StopsJson()
    runsJson = RunsJson()
  stopsJson.load_stops()
  runsJson.load_runs()
  stops = Stops(runsJson, stopsJson, dayNumber)
  stops.load_stops()
  stops.save_changes()
  
