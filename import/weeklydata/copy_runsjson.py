#!/usr/bin/env python

import json
import sys

# run json example {"firstCheckPoint":"/runs/0/checkpoints/9303","firstStop":"/stops/4631","id":"0","route":"/routes/160","url":"/runs/0"}

class RunsJson:
  filename = None
  startingRunId = 0
  runs = None
  dayNumber = 0

  def __init__(self, filename="runs_day0.json", dayNumber=1):
    self.filename = filename
    self.dayNumber = dayNumber


  def load_runs(self):
    # Load runs info
    json_data=open(self.filename)
    data = json.load(json_data)
    self.runs = runs = data['runsObjectsList']
    self.startingRunId = len(runs) * self.dayNumber 
    print "runs: " + str(len(runs))
    return self.runs
      
