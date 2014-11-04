#!/usr/bin/env python

import json
import sys

if (len(sys.argv) > 1):
  json_data=open(sys.argv[1])
else:
  json_data=open('runs_day0.json')

data = json.load(json_data)
runs = data['runsObjectsList']

greaterRun = 0
for r in runs:
  runId = int(r['id'])
  greater = (runId > greaterRun)
  print "%d > %d? %r" %(runId, greaterRun, greater)
  if (greater):
    greaterRun = runId

print "runs number is: %d" %len(runs)
print "greater ID: %d" %greaterRun
