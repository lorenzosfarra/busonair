#!/usr/bin/env python

import json
import sys

if (len(sys.argv) > 1):
  json_data=open(sys.argv[1])
else:
  json_data=open('stops.json')

data = json.load(json_data)
stops = data['stopsObjectsList']

greaterStop = 0
for s in stops:
  stopId = int(s['id'])
  greater = (stopId > greaterStop)
  print "%d > %d? %r" %(stopId, greaterStop, greater)
  if (greater):
    greaterStop = stopId

print "stops number is: %d" %len(stops)
print "greater ID: %d" %greaterStop
