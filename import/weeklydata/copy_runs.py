#!/usr/bin/env python

import json
import sys
from copy_runsjson import RunsJson
from copy_stopsjson import StopsJson

# run json example {"firstCheckPoint":"/runs/0/checkpoints/9303","firstStop":"/stops/4631","id":"0","route":"/routes/160","url":"/runs/0"}

class Runs:
  dayNumber = 1
  startingRunId = 0
  startingStopId = 0
  newRuns = None
  oldRuns = None

  def __init__(self, runsJSON, stopsJson, dayNumber=1):
    self.dayNumber = dayNumber
    self.startingStopId = stopsJson.startingStopId
    self.startingRunId = runsJson.startingRunId
    self.oldRuns = runsJSON.runs

  def load_runs(self):
    runs = self.oldRuns
    self.newRuns = []
    print "startingRunId %d, startingStopId %d" %(self.startingRunId, self.startingStopId)
    for r in runs:
      curRunId = int(r['id'])
      r['id'] = str(curRunId + self.startingRunId)
      firstStop = int(r['firstStop'])
      newStop = firstStop + self.startingStopId
      r['firstStop'] = str(newStop)
      print "firstStop: %d -> %d" %(firstStop, newStop)
      self.newRuns.append(r);

  def save_changes(self):
    filename = "jsons/runs_day%d.json" %(self.dayNumber)
    with open(filename, 'w') as outfile:
      json.dump({"runsObjectsList": self.newRuns}, outfile)


if __name__ == '__main__':
  runs = runsJson = stopsJson = None
  if len(sys.argv) > 1:
    dayNumber = int(sys.argv[1])
    if len(sys.argv) > 2:
      runsJson = RunsJson(sys.argv[2], dayNumber)
      stopsJson = StopsJson(dayNumber=dayNumber)
    else:
      runsJson = RunsJson(dayNumber=dayNumber)
      stopsJson = StopsJson(dayNumber=dayNumber)
  else:
    runsJson = RunsJson()
    stopsJson = StopsJson()
    dayNumber = 1
  
  runsJson.load_runs();
  stopsJson.load_stops();
  runs = Runs(runsJson, stopsJson, dayNumber)
  runs.load_runs()
  runs.save_changes()
  print "runsJson.startingRunId: %d" %runsJson.startingRunId
  print "stopsJson.startingStopId: %d" %stopsJson.startingStopId
 
      
